package io.github.toolfactory.narcissus;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class NarcissusTest {
    static class X {
        int triple(int x) {
            return x * 3;
        }
    }

    @Test
    public void testInvokeIntMethodWithParam() throws Exception {
        Method triple = Narcissus.findMethod(X.class, "triple", int.class);
        assertThat(Narcissus.invokeIntMethod(new X(), triple, 5)).isEqualTo(15);
    }

    static class Y {
        int i = 1;
        long j = 2;
        short s = 3;
        char c = '4';
        byte b = 5;
        boolean z = true;
        float f = 7.0f;
        double d = 8.0;

        static int _i = 1;
        static long _j = 2;
        static short _s = 3;
        static char _c = '4';
        static byte _b = 5;
        static boolean _z = true;
        static float _f = 7.0f;
        static double _d = 8.0;
    }

    @Test
    public void testFieldGetters() throws Exception {
        Y y = new Y();
        for (Field f : Y.class.getDeclaredFields()) {
            Field nf = Narcissus.findField(Y.class, f.getName());
            assertThat(nf).isEqualTo(f);
            if (Modifier.isStatic(f.getModifiers())) {
                assertThat(Narcissus.getStaticField(nf)).isEqualTo(f.get(null));
            } else {
                assertThat(Narcissus.getField(y, nf)).isEqualTo(f.get(y));
            }
        }
    }

    @Test
    public void testFieldSetters() throws Exception {
        Y y = new Y();

        Field i = Narcissus.findField(Y.class, "i");
        assertThat(Narcissus.getField(y, i)).isEqualTo(1);
        Narcissus.setField(y, i, 2);
        assertThat(Narcissus.getField(y, i)).isEqualTo(2);

        Field _i = Narcissus.findField(Y.class, "_i");
        assertThat(Narcissus.getStaticField(_i)).isEqualTo(1);
        Narcissus.setStaticField(_i, 2);
        assertThat(Narcissus.getStaticField(_i)).isEqualTo(2);
    }

    static class Z {
        int i() {
            return 1;
        }

        long j() {
            return 2;
        }

        short s() {
            return 3;
        }

        char c() {
            return '4';
        }

        byte b() {
            return 5;
        }

        boolean z() {
            return true;
        }

        float f() {
            return 7.0f;
        }

        double d() {
            return 8.0;
        }

        static int _i() {
            return 1;
        }

        static long _j() {
            return 2;
        }

        static short _s() {
            return 3;
        }

        static char _c() {
            return '4';
        }

        static byte _b() {
            return 5;
        }

        static boolean _z() {
            return true;
        }

        static float _f() {
            return 7.0f;
        }

        static double _d() {
            return 8.0;
        }
    }

    @Test
    public void testInvokeMethods() throws Exception {
        Z z = new Z();
        for (Method m : Z.class.getDeclaredMethods()) {
            Method nm = Narcissus.findMethod(Z.class, m.getName());
            assertThat(nm).isEqualTo(m);
            if (Modifier.isStatic(m.getModifiers())) {
                assertThat(Narcissus.invokeStaticMethod(nm)).isEqualTo(m.invoke(null));
            } else {
                assertThat(Narcissus.invokeMethod(z, nm)).isEqualTo(m.invoke(z));
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNullPointerExceptionNonStatic() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeDoubleMethod(null, dm);
    }

    @Test(expected = NullPointerException.class)
    public void testCheckNullPointerExceptionStatic() throws Exception {
        Method _dm = Narcissus.findMethod(Z.class, "_d");
        Narcissus.invokeDoubleMethod(null, _dm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckStaticModifierException1() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeStaticDoubleMethod(dm);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckObjectClassDoesNotMatchDeclaringClass() throws Exception {
        Method dm = Narcissus.findMethod(Z.class, "d");
        Narcissus.invokeDoubleMethod(new Y(), dm);
    }

    @Test
    public void testFindClass() throws Exception {
        Class<?> cls = Narcissus.findClass(Y.class.getName());
        assertThat(cls).isNotNull();
        assertThat(cls.getName()).isEqualTo(Y.class.getName());
        Class<?> arrCls = Narcissus.findClass(Y.class.getName() + "[]");
        assertThat(arrCls).isNotNull();
        assertThat(arrCls.getName()).isEqualTo("[L" + Y.class.getName() + ";");
    }

    @Test
    public void testEnumerateFields() throws Exception {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : Narcissus.enumerateFields(Y.class)) {
            fieldNames.add(field.getName());
        }
        assertThat(fieldNames).containsOnly("i", "j", "s", "c", "b", "z", "f", "d", "_i", "_j", "_s", "_c", "_b",
                "_z", "_f", "_d");
    }

    @Test
    public void testEnumerateMethods() throws Exception {
        List<String> methodNames = new ArrayList<>();
        for (Method method : Narcissus.enumerateMethods(Z.class)) {
            methodNames.add(method.getName());
        }
        assertThat(methodNames).contains("i", "j", "s", "c", "b", "z", "f", "d", "_i", "_j", "_s", "_c", "_b", "_z",
                "_f", "_d");
    }

    static class A {
        int x;

        int y() {
            return x + 1;
        }
    }

    static class B extends A {
    }

    @Test
    public void testInheritedField() throws Exception {
        Field ax = Narcissus.findField(A.class, "x");
        assertThat(ax).isNotNull();
        Field bx = Narcissus.findField(B.class, "x");
        assertThat(bx).isNotNull();
        A a = new A();
        a.x = 3;
        assertThat(Narcissus.getIntField(a, ax)).isEqualTo(a.x);
        B b = new B();
        b.x = 5;
        assertThat(Narcissus.getIntField(b, bx)).isEqualTo(b.x);
    }

    @Test
    public void testInheritedMethod() throws Exception {
        Method ay = Narcissus.findMethod(A.class, "y");
        assertThat(ay).isNotNull();
        Method by = Narcissus.findMethod(B.class, "y");
        assertThat(by).isNotNull();
        A a = new A();
        a.x = 3;
        assertThat(Narcissus.invokeIntMethod(a, ay)).isEqualTo(a.x + 1);
        B b = new B();
        b.x = 5;
        assertThat(Narcissus.invokeIntMethod(b, by)).isEqualTo(b.x + 1);
    }

    static class C {
        A a;
        B b;

        static A identA(A aVal) {
            return aVal;
        }

        static B identB(B bVal) {
            return bVal;
        }
    }

    @Test
    public void testAssignFieldSubtype() throws Exception {
        C c = new C();
        Narcissus.setField(c, Narcissus.findField(C.class, "a"), new B());
        assertThat(c.a instanceof B);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAssignFieldSupertype() throws Exception {
        C c = new C();
        Narcissus.setField(c, Narcissus.findField(C.class, "b"), new A());
    }

    @Test
    public void testCallWithParamSubtype() throws Exception {
        A retVal = (A) Narcissus.invokeStaticObjectMethod(Narcissus.findMethod(C.class, "identA", A.class),
                new B());
        assertThat(retVal instanceof B);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallWithParamSupertype() throws Exception {
        Narcissus.invokeStaticObjectMethod(Narcissus.findMethod(C.class, "identB", B.class), new A());
    }
}