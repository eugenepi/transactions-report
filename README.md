Run tests:<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; mvnw test<br />

Build project:<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; mvnw package<br />

Build docker:<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; docker build -t transaction-service .<br />

Run docker compose:<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; docker-compose up<br />

Deployment to Kubernetes:<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; kompose convert -f docker-compose.yml<br />
   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; kubectl apply .<br />


As 3rd party exchange rate api was selected https://www.exchangerate-api.com/.<br />
Reason to use their API is response that contains all available currencies to selected one.<br />
It means we need to do just one request.<br />
