package randomizedtest;

import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
  @Test
  public void testThreeAddThreeRemove() {
      AListNoResizing<Integer> a = new AListNoResizing<>();
      BuggyAList<Integer> b = new BuggyAList<>();

      a.addLast(4);
      b.addLast(4);

      a.addLast(5);
      b.addLast(5);

      a.addLast(6);
      b.addLast(6);

      assertEquals(a.size(), b.size());

      assertEquals(a.removeLast(), b.removeLast());
      assertEquals(a.removeLast(), b.removeLast());
      assertEquals(a.removeLast(), b.removeLast());

  }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        Random ran = new Random();
        for (int i = 0; i < N; i += 1) {
            int operationNumber = ran.nextInt(4) + 0;;
            if (operationNumber == 0) {
                // addLast
                int randVal = ran.nextInt(100) + 0;
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 2) {
                if(L.size() <= 0 || B.size() <= 0) {
                    continue;
                }
                assertEquals(L.getLast(), B.getLast());
            } else {
                if(L.size() <= 0 || B.size() <= 0) {
                    continue;
                }
                assertEquals(L.removeLast(), B.removeLast());
            }

        }

    }
}
