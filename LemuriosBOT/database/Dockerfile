FROM postgres:latest

COPY ./schema/*.sql /lemurios_schema/

ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=password
ENV POSTGRES_DB=LemuriosBotDatabase

EXPOSE 5432