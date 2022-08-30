package Dog;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class DogTest {
    @Test
    public void testSmall() {
        Dog d = new Dog(3);
        assertEquals("yip", d.noise());
    }

    @Test
    public void testLarge() {
        Dog d = new Dog(20);
        assertEquals("bark", d.noise());
    }
}
