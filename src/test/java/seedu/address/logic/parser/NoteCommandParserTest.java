package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INDEX_NOT_IN_LIST;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.NoteCommand;
import seedu.address.model.person.Note;

public class NoteCommandParserTest {

    private final NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_indexAndText_returnsNoteCommand() {
        assertParseSuccess(parser, "1 Based in London",
                new NoteCommand(INDEX_FIRST_PERSON, new Note("Based in London")));
        assertParseSuccess(parser, "  1   Based in London  ",
                new NoteCommand(INDEX_FIRST_PERSON, new Note("Based in London")));
    }

    @Test
    public void parse_textWithPrefixes_keptAsIs() {
        assertParseSuccess(parser, "1 likes t/ and n/ in notes",
                new NoteCommand(INDEX_FIRST_PERSON, new Note("likes t/ and n/ in notes")));
    }

    @Test
    public void parse_indexOnly_returnsRemoveCommand() {
        assertParseSuccess(parser, "1", new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY));
        assertParseSuccess(parser, " 1  ", new NoteCommand(INDEX_FIRST_PERSON, Note.EMPTY));
    }

    @Test
    public void parse_missingOrInvalidIndex_throwsParseException() {
        for (String args : List.of("", "   ", "abc")) {
            assertParseFailure(parser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }
        for (String args : List.of("0 note", "-1 note")) {
            assertParseFailure(parser, args, MESSAGE_INDEX_NOT_IN_LIST);
        }
    }
}
