package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalClientBook;

import org.junit.jupiter.api.Test;

import seedu.address.model.ClientBook;
import seedu.address.model.ContactView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.project.Project;

public class ClearCommandTest {

    @Test
    public void execute_emptyClientBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyClientBook_success() {
        Model model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalClientBook(), new UserPrefs());
        expectedModel.setClientBook(new ClientBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_whileViewingProject_resetsContactView() {
        Model model = new ModelManager(getTypicalClientBook(), new UserPrefs());
        Project project = new Project("Client Portal");
        model.addProject(project);
        model.showProjectClients(project);

        new ClearCommand().execute(model);

        assertEquals(ContactView.allContacts(), model.getContactView());
    }

}
