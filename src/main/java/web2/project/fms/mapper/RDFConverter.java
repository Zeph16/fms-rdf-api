package web2.project.fms.mapper;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import web2.project.fms.model.*;

import java.util.UUID;

public class RDFConverter {

    private static final String BASE_URI = "http://example.com/fintech/";

    public static Model accountToRDF(Account account) {
        Model model = new LinkedHashModel();
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI accountIRI = factory.createIRI(BASE_URI, "account/" + account.getId());

        model.add(accountIRI, factory.createIRI(BASE_URI, "username"), factory.createLiteral(account.getUsername()));
        model.add(accountIRI, factory.createIRI(BASE_URI, "name"), factory.createLiteral(account.getName()));
        model.add(accountIRI, factory.createIRI(BASE_URI, "balance"), factory.createLiteral(account.getBalance()));

        printModel(model);
        return model;
    }

    public static Model budgetToRDF(Budget budget) {
        Model model = new LinkedHashModel();
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI budgetIRI = factory.createIRI(BASE_URI, "budget/" + budget.getId());
        IRI accountIRI = factory.createIRI(BASE_URI, "account/" + budget.getAccount().getId());
        IRI categoryIRI = factory.createIRI(BASE_URI, "category/" + budget.getCategory().getId());

        model.add(budgetIRI, factory.createIRI(BASE_URI, "name"), factory.createLiteral(budget.getName()));
        model.add(budgetIRI, factory.createIRI(BASE_URI, "amount"), factory.createLiteral(budget.getAmount()));
        model.add(budgetIRI, factory.createIRI(BASE_URI, "startDateTime"), factory.createLiteral(budget.getStartDateTime().toString()));
        model.add(budgetIRI, factory.createIRI(BASE_URI, "endDateTime"), factory.createLiteral(budget.getEndDateTime().toString()));
        model.add(budgetIRI, factory.createIRI(BASE_URI, "account"), accountIRI);
        model.add(budgetIRI, factory.createIRI(BASE_URI, "category"), categoryIRI);

        printModel(model);
        return model;
    }

    public static Model categoryToRDF(Category category) {
        Model model = new LinkedHashModel();
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI categoryIRI = factory.createIRI(BASE_URI, "category/" + category.getId());
        IRI accountIRI = factory.createIRI(BASE_URI, "account/" + category.getAccount().getId());

        model.add(categoryIRI, factory.createIRI(BASE_URI, "name"), factory.createLiteral(category.getName()));
        model.add(categoryIRI, factory.createIRI(BASE_URI, "description"), factory.createLiteral(category.getDescription()));
        model.add(categoryIRI, factory.createIRI(BASE_URI, "account"), accountIRI);

        printModel(model);
        return model;
    }

    public static Model goalToRDF(Goal goal) {
        Model model = new LinkedHashModel();
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI goalIRI = factory.createIRI(BASE_URI, "goal/" + goal.getId());
        IRI accountIRI = factory.createIRI(BASE_URI, "account/" + goal.getAccount().getId());
        IRI categoryIRI = factory.createIRI(BASE_URI, "category/" + goal.getCategory().getId());

        model.add(goalIRI, factory.createIRI(BASE_URI, "name"), factory.createLiteral(goal.getName()));
        model.add(goalIRI, factory.createIRI(BASE_URI, "description"), factory.createLiteral(goal.getDescription()));
        model.add(goalIRI, factory.createIRI(BASE_URI, "amount"), factory.createLiteral(goal.getAmount()));
        model.add(goalIRI, factory.createIRI(BASE_URI, "account"), accountIRI);
        model.add(goalIRI, factory.createIRI(BASE_URI, "category"), categoryIRI);

        printModel(model);
        return model;
    }

    public static Model transactionToRDF(Transaction transaction) {
        Model model = new LinkedHashModel();
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        IRI transactionIRI = factory.createIRI(BASE_URI, "transaction/" + transaction.getId());
        IRI accountIRI = factory.createIRI(BASE_URI, "account/" + transaction.getAccount().getId());
        IRI categoryIRI = factory.createIRI(BASE_URI, "category/" + transaction.getCategory().getId());

        model.add(transactionIRI, factory.createIRI(BASE_URI, "amount"), factory.createLiteral(transaction.getAmount()));
        model.add(transactionIRI, factory.createIRI(BASE_URI, "description"), factory.createLiteral(transaction.getDescription()));
        model.add(transactionIRI, factory.createIRI(BASE_URI, "creationDateTime"), factory.createLiteral(transaction.getCreationDateTime().toString()));
        model.add(transactionIRI, factory.createIRI(BASE_URI, "account"), accountIRI);
        model.add(transactionIRI, factory.createIRI(BASE_URI, "category"), categoryIRI);
        model.add(transactionIRI, factory.createIRI(BASE_URI, "type"), factory.createLiteral(transaction.getType().toString()));

        printModel(model);
        return model;
    }


    public static IRI accountIRI(UUID id) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(BASE_URI, "account/" + id);
    }

    public static IRI budgetIRI(UUID id) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(BASE_URI, "budget/" + id);
    }

    public static IRI categoryIRI(UUID id) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(BASE_URI, "category/" + id);
    }

    public static IRI goalIRI(UUID id) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(BASE_URI, "goal/" + id);
    }

    public static IRI transactionIRI(UUID id) {
        SimpleValueFactory factory = SimpleValueFactory.getInstance();
        return factory.createIRI(BASE_URI, "transaction/" + id);
    }

    public static void printModel(Model model) {
        // Iterate over statements in the model
        for (Statement statement : model) {
            // Get subject, predicate, and object of the statement
            Resource subject = statement.getSubject();
            org.eclipse.rdf4j.model.IRI predicate = statement.getPredicate();
            org.eclipse.rdf4j.model.Value object = statement.getObject();

            // Print out the statement
            System.out.println("Statement: " + subject + " " + predicate + " " + object);
        }
    }
}