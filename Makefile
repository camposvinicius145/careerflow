.PHONY: run test verify up down

run:
	./mvnw spring-boot:run

test:
	./mvnw test

verify:
	./mvnw verify -Pintegration-tests

up:
	docker compose up --build

down:
	docker compose down
