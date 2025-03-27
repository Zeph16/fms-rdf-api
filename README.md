# **Financial Management API**

A **Spring Boot REST API** for managing **accounts, transactions, budgets, goals, and categories**, built with **PostgreSQL** and featuring **JWT-based authentication**. This project demonstrates **standard REST API best practices**, security mechanisms, and **semantic web integration using RDF and SPARQL**.

## **Features**

✅ **RESTful API** – Standard CRUD operations following best practices and Spring Boot design patterns (controller-service-repository, DTOs, custom exceptions...).  
✅ **Spring Security with JWT** - Custom JWT functionality with a **token database** for tracking and revocation, along with a custom authentication filter for handling JWTs.         
✅ **Swagger API Documentation** – Explore and test endpoints.  
✅ **PostgreSQL Database** – Reliable structured storage.  
✅ **RDF Triple Store (Apache Jena Fuseki)** – Financial data is also stored in **RDF format** for **semantic queries** and **linked data capabilities**.  
✅ **Docker Support** – PostgreSQL and Fuseki are set up via `docker-compose.yml`.


## **Getting Started**

### **Prerequisites**

- **Java 21+**
    
- **Maven**
    
- **Docker & Docker Compose**
    

### **Installation**

1. Clone the repository:
    
    ```sh
    git clone https://github.com/Zeph16/fms-rdf-api.git
    cd fms-rdf-api
    ```
    
2. Start **PostgreSQL** and **Fuseki**:
    
    ```sh
    docker-compose up -d
    ```
    
3. Run the application:
    
    ```sh
    mvn spring-boot:run
    ```
    
4. Access API documentation at:
    
    - **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
        
    - **SPARQL Query UI** (for RDF data): [http://localhost:3030](http://localhost:3030)
        

## **Semantic Web & RDF Integration**

This API extends traditional financial management by **storing financial data as RDF triples** in a **triple store (Apache Jena Fuseki)**.

### **Why RDF?**

🔹 **Structured & Queryable Linked Data** – Relationships between accounts, budgets, and transactions can be queried using **SPARQL**, instead of relying solely on SQL joins.

🔹 **Semantic Enrichment** – RDF allows integration with external **financial ontologies** for enhanced insights.

🔹 **Graph-Based Queries** – SPARQL enables **pattern-based** retrieval, making it possible to analyze **financial relationships** beyond just tabular data.

### **Example Use Cases**

- **Retrieve all budgets linked to a specific account.**
    
- **Identify spending patterns across multiple categories.**
    
- **Connect financial data with external knowledge graphs (e.g., taxonomies, industry standards).**
    
- **Enable AI-driven financial insights using linked data.**
    

Currently, the application **writes financial data to Fuseki** in RDF format but does not yet support querying. Future improvements may include **SPARQL-based financial analysis** and **ontology-driven classification**.

## **Future Improvements**

🚀 **SPARQL Query Implementation** – Enable data retrieval using semantic queries.  
🚀 **Ontology Integration** – Leverage financial industry ontologies for richer insights.  
🚀 **Enhanced Security** – Introduce more granular user permissions and RBAC.
