import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {
    private static Solution solver;

    @BeforeAll
    public static void beforeAll() {
        solver = new Solution();
    }

    @Test
    public void twoSameArraysTest() {
        String[] first = {"шуруп", "краска", "обои"};

        List<String> actual = solver.solve(first, first);

        assertEquals(3, actual.size());
        for (String s : first) {
            assertTrue(actual.contains(s + ':' + s));
        }
    }

    @Test
    public void simpleLogicTest() {
        String[] first = {"шуруп", "краска", "обои"};
        String[] second = {"краска синяя", "обои рулон", "шуруп 3x15"};

        List<String> actual = solver.solve(first, second);

        assertEquals(3, actual.size());

        assertTrue(actual.contains("шуруп:шуруп 3x15"));
        assertTrue(actual.contains("краска:краска синяя"));
        assertTrue(actual.contains("обои:обои рулон"));
    }

    @Test
    public void notEqualSizeWordsSetsTest() {
        String[] first = {"шуруп"};
        String[] second = {"краска синяя", "обои рулон", "шуруп 3x15"};

        List<String> actual = solver.solve(first, second);

        assertEquals(3, actual.size());

        assertTrue(actual.contains("шуруп:шуруп 3x15"));
        assertTrue(actual.contains("краска синяя:?"));
        assertTrue(actual.contains("обои рулон:?"));
    }

    @Test
    public void firstWordsSetBiggerTest() {
        String[] first = {"краска синяя", "обои рулон", "шуруп 3x15"};
        String[] second = {"шуруп"};

        List<String> actual = solver.solve(first, second);

        assertEquals(3, actual.size());

        assertTrue(actual.contains("шуруп 3x15:шуруп"));
        assertTrue(actual.contains("краска синяя:?"));
        assertTrue(actual.contains("обои рулон:?"));
    }

    @Test
    public void emptyWordsSetTest() {
        String[] first = {"краска синяя", "обои рулон", "шуруп 3x15"};
        String[] second = {};

        List<String> actual = solver.solve(first, second);

        assertEquals(3, actual.size());

        assertTrue(actual.contains("шуруп 3x15:?"));
        assertTrue(actual.contains("краска синяя:?"));
        assertTrue(actual.contains("обои рулон:?"));
    }
}
