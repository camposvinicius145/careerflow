(() => {
    const sidebar = document.querySelector("#sidebar");
    const menuToggle = document.querySelector("[data-menu-toggle]");

    menuToggle?.addEventListener("click", () => sidebar?.classList.toggle("open"));
    document.addEventListener("click", (event) => {
        if (
            sidebar?.classList.contains("open")
            && !sidebar.contains(event.target)
            && !menuToggle?.contains(event.target)
        ) {
            sidebar.classList.remove("open");
        }
    });

    document.querySelectorAll("form[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });

    const demoButton = document.querySelector("[data-demo-login]");
    demoButton?.addEventListener("click", () => {
        const email = document.querySelector("input[name='username']");
        const password = document.querySelector("input[name='password']");
        if (email && password) {
            email.value = "demo@careerflow.dev";
            password.value = "demo123";
            password.focus();
        }
    });

    setupKanban();

    function setupKanban() {
        const cards = document.querySelectorAll(".job-card[draggable='true']");
        const columns = document.querySelectorAll(".kanban-column[data-status]");
        if (!cards.length || !columns.length) return;

        let draggedCard = null;

        cards.forEach((card) => {
            card.addEventListener("dragstart", () => {
                draggedCard = card;
                card.classList.add("dragging");
            });

            card.addEventListener("dragend", () => {
                card.classList.remove("dragging");
                columns.forEach((column) => column.classList.remove("drag-over"));
                draggedCard = null;
            });
        });

        columns.forEach((column) => {
            column.addEventListener("dragover", (event) => {
                event.preventDefault();
                column.classList.add("drag-over");
            });

            column.addEventListener("dragleave", (event) => {
                if (!column.contains(event.relatedTarget)) {
                    column.classList.remove("drag-over");
                }
            });

            column.addEventListener("drop", async (event) => {
                event.preventDefault();
                column.classList.remove("drag-over");
                if (!draggedCard || draggedCard.closest(".kanban-column") === column) return;

                const originalColumn = draggedCard.closest(".kanban-column");
                column.querySelector(".column-dropzone")?.appendChild(draggedCard);
                updateCounts(originalColumn, column);

                try {
                    await moveApplication(draggedCard.dataset.applicationId, column.dataset.status);
                    window.setTimeout(() => window.location.reload(), 240);
                } catch {
                    originalColumn.querySelector(".column-dropzone")?.appendChild(draggedCard);
                    updateCounts(column, originalColumn);
                    const error = document.querySelector("[data-board-error]");
                    if (error) error.hidden = false;
                }
            });
        });
    }

    async function moveApplication(id, status) {
        const token = document.querySelector("meta[name='_csrf']")?.content;
        const header = document.querySelector("meta[name='_csrf_header']")?.content;
        const headers = { "Content-Type": "application/json" };
        if (token && header) headers[header] = token;

        const response = await fetch(`/api/v1/applications/${id}/status`, {
            method: "PATCH",
            headers,
            body: JSON.stringify({ status })
        });
        if (!response.ok) throw new Error("Move failed");
    }

    function updateCounts(...columns) {
        columns.forEach((column) => {
            const count = column.querySelectorAll(".job-card").length;
            const counter = column.querySelector(".column-count");
            if (counter) counter.textContent = count;
            const empty = column.querySelector(".empty-column-card");
            if (empty) empty.hidden = count > 0;
        });
    }
})();
