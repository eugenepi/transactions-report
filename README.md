Run tests:
    mvnw test

Build project:
    mvnw package

Build docker:
    docker build -t transaction-service .

Run docker compose:
    docker-compose up

Deployment to Kubernetes:
    kompose convert -f docker-compose.yml
    kubectl apply .


As 3rd party exchange rate api was selected https://www.exchangerate-api.com/.
Reason to use their API is response that contains all available currencies to selected one.
It means we need to do just one request.
