package com.my.project;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.my.project.matcher.FieldMatcher;

@SuppressWarnings("unchecked")
//@RunWith( org.mockito.runners.MockitoJUnitRunner.class )
public class MockTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * 验证代码行为
     * @throws Exception
     */
    @Test
    public void testVerify() {
        //模拟一个List实例
        List<String> mockedList = mock(List.class);

        //使用模拟对象
        mockedList.add("one");
        mockedList.clear();

        //验证对象操作
        verify(mockedList).add("one");
        verify(mockedList).clear();
    }

    /**
     * 模拟对象数据
     */
    @Test
    @Ignore
    public void testStubbing() {
        //模拟一个类实例
        LinkedList<String> mockedList = mock(LinkedList.class);

        //模拟对象数据
        when(mockedList.get(0)).thenReturn("first");
        when(mockedList.get(1)).thenThrow(new RuntimeException("get element failed!"));

        //following prints "first"
        assertEquals("first", mockedList.get(0));

        //测试异常，可以验证Exception的message内容（包含）
        //另外一种方法： 在@Test的expected属性中指定本方法会抛出的异常
        thrown.expect(RuntimeException.class);
        thrown.expectMessage("get element failed");
        //following throws runtime exception
        mockedList.get(1);

        //following prints "null" because get(999) was not stubbed
        assertNull(mockedList.get(999));

        //只在需要的时候模拟对象数据
        //Although it is possible to verify a stubbed invocation, usually it's just redundant
        //If your code cares what get(0) returns then something else breaks (often before even verify() gets executed).
        //If your code doesn't care what get(0) returns then it should not be stubbed. Not convinced? See here.
        verify(mockedList).get(0);
    }

    /**
     * 参数匹配器
     */
    @Test
    public void testArgumentMatcher1() {
        //模拟一个List实例
        List<String> mockedList = mock(List.class);
        Book book = mock(Book.class);

        //模拟List的get方法参数可以是任意整数
        when(mockedList.get(anyInt())).thenReturn("element");

        //如果使用了参数匹配器，那么所有的参数需要由匹配器来提供，如下eq()方法也是一个参数匹配器
        //不能写成： when(book.setInfo(anyString(), anyString(), "Publication")).thenReturn("failed");
        when(book.setInfo(anyString(), anyString(), eq("Publication"))).thenReturn("failed");

        System.out.println(mockedList.get(999));
        System.out.println(book.setInfo("ISBN", "TITLE", "PUBLICATION"));

        //验证的时候也可以使用参数匹配器
        verify(mockedList).get(anyInt());
        verify(book).setInfo(anyString(), anyString(), anyString());
    }

    /**
     * 自定义类型的参数匹配（使用Matcher）
     */
    @Test
    public void testArgumentMatcher2() {

        //自定类型的参数匹配
        BookDAL mockedBookDAL = mock(BookDAL.class);

        mockedBookDAL.setBookTitle(new Book("Original Title"), "New Title");

        //验证参数Book的title属性值属性必须为Original Title
        verify(mockedBookDAL).setBookTitle(argThat(FieldMatcher.fieldMatcher(new Function<Book, Object>() {
            public Object apply(Book t) {
                return t.getTitle();
            }
        }, "Original Title")), eq("New Title"));

    }

    /**
     * 自定义类型的参数匹配（使用Captor，不要与模拟数据同时使用，如果需要模拟数据可以自定义ArgumentMatcher）
     */
    @Test
    public void testArgumentMatcher3() {

        //自定类型的参数匹配
        BookDAL mocked1 = mock(BookDAL.class);
        BookDAL mocked2 = mock(BookDAL.class);

        mocked1.setBookTitle(new Book("Original Title"), "New Title");

        mocked2.setBookTitle(new Book("Original Title1"), "New Title");
        mocked2.setBookTitle(new Book("Original Title2"), "New Title");
        mocked2.setBookTitle(new Book("Original Title3"), "New Title");

        ArgumentCaptor<Book> argument = ArgumentCaptor.forClass(Book.class);

        //验证参数Book的title属性值属性必须为Original Title
        verify(mocked1).setBookTitle(argument.capture(), eq("New Title"));
        assertEquals("Original Title", argument.getValue().getTitle());

        //清除上一次捕获的参数数据
        argument.getAllValues().clear();

        //方法多次调用： 验证参数Book的title属性值Original Title
        verify(mocked2, times(3)).setBookTitle(argument.capture(), eq("New Title"));
        List<Book> books = argument.getAllValues();
        assertEquals("Original Title3", argument.getValue().getTitle());
        assertEquals("Original Title1", books.get(0).getTitle());
        assertArrayEquals(new String[] { "Original Title1", "Original Title2", "Original Title3" }, new String[] {
            books.get(0).getTitle(),
            books.get(1).getTitle(),
            books.get(2).getTitle() });
    }

    /**
     * 验证方法调用次数
     */
    @Test
    public void testNumberOfInvocations() {

        //模拟一个List实例
        List<String> mockedList = mock(List.class);

        mockedList.add("once");

        mockedList.add("twice");
        mockedList.add("twice");

        mockedList.add("three times");
        mockedList.add("three times");
        mockedList.add("three times");

        //判断调用次数时，会同时判断方法参数 equals or not

        //如果不指定调用次数即是认为调用1次
        verify(mockedList).add("once");
        verify(mockedList, times(1)).add("once");

        //指定调用次数
        verify(mockedList, times(2)).add("twice");
        verify(mockedList, times(3)).add("three times");

        //从示调用，相当于times(0)
        verify(mockedList, never()).add("never happened");

        //至少调用1次，相当于atLeast(1)
        verify(mockedList, atLeastOnce()).add("three times");
        //至少调用2次
        //verify(mockedList, atLeast(2)).add("five times");
        //至多调用5次
        verify(mockedList, atMost(5)).add("three times");

    }

    /**
     * 对于返回值为void的方法模拟抛出Exception
     */
    @Test
    public void testVoidMethodWithException() {

        //模拟一个List实例
        List<String> mockedList = mock(List.class);

        doThrow(new RuntimeException()).when(mockedList).clear();

        thrown.expect(RuntimeException.class);
        mockedList.clear();

    }
    
    /**
     * doXXX()系列方法
     */
    @Test
    public void testDoXXX() {
        
        //doThrow(Throwable) 

        //模拟一个List实例
        List<String> mockedList = mock(List.class);
        doThrow(new RuntimeException()).when(mockedList).clear();
        
        thrown.expect(RuntimeException.class);
        //following throws RuntimeException:
        mockedList.clear();
        
        //doReturn(Object)
        
        List<String> list = new LinkedList<String>();
        List<String> spy = spy(list);
        //下面代码会直接抛出IndexOutOfBoundsException，因为list是空的
        thrown.expect(IndexOutOfBoundsException.class);
        when(spy.get(0)).thenReturn("foo");
        //可以使用doReturn来返回就不会抛出异常了
        doReturn("foo").when(spy).get(0);
        
        Book book = new Book();
        Book spyBook = spy(book);
        when(spyBook.getTitle()).thenThrow(new RuntimeException());
        //如下调用会直接返回RuntimeException
        thrown.expect(RuntimeException.class);
        when(spyBook.getTitle()).thenReturn("bar");
        //使用doReturn可以避免之前模拟的招聘异常的行为发生
        doReturn("bar").when(spyBook).getTitle();

        //doAnswer(Answer)
        
        Book bookAnswer = mock(Book.class);
        doAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) {
                return "TITLE";
            }})
        .when(bookAnswer).getTitle();
        assertEquals("TITLE", bookAnswer.getTitle());
        
        //doNothing()
        
        doNothing().doThrow(new RuntimeException()).when(mockedList).clear();
        //第一次调用时不做任何操作
        mockedList.clear();
        //第二次调用时抛出RuntimeException
        thrown.expect(RuntimeException.class);
        mockedList.clear();

        //调用clear()方法时不做任何操作
        doNothing().when(spy).clear();
        spy.add("one");
        //clear()不做操作，所以spy里仍然有"one"元素
        spy.clear();
        assertTrue(spy.contains("one"));

        //doCallRealMethod()

        doCallRealMethod().when(mockedList).clear();
        //调用该实例的直正的clear()方法
        mockedList.clear();
        assertTrue(mockedList.size() == 0);

    }

    /**
     * 对代码执行顺序进行验证
     */
    @Test
    public void testVerifyInOrder() {
        // A. Single mock whose methods must be invoked in a particular order
        List<String> singleMock = mock(List.class);

        //using a single mock
        singleMock.add("was added first");
        singleMock.add("was added second");

        //create an inOrder verifier for a single mock
        InOrder inOrder1 = inOrder(singleMock);

        //following will make sure that add is first called with "was added first, then with "was added second"
        inOrder1.verify(singleMock).add("was added first");
        inOrder1.verify(singleMock).add("was added second");

        // B. Multiple mocks that must be used in a particular order
        List<String> firstMock = mock(List.class);
        List<String> secondMock = mock(List.class);

        //using mocks
        firstMock.add("was called first");
        secondMock.add("was called second");

        //create inOrder object passing any mocks that need to be verified in order
        InOrder inOrder2 = inOrder(firstMock, secondMock);

        //following will make sure that firstMock was called before secondMock
        inOrder2.verify(firstMock).add("was called first");
        inOrder2.verify(secondMock).add("was called second");

        // Oh, and A + B can be mixed together at will

    }

    /**
     * 验证对象操作没有发生
     */
    @Test
    public void testInteractionNeverHappenOnMock() {
        List<String> mockOne = mock(List.class);
        List<String> mockTwo = mock(List.class);
        List<String> mockThree = mock(List.class);

        //using mocks - only mockOne is interacted
        mockOne.add("one");

        //ordinary verification
        verify(mockOne).add("one");

        //verify that method was never called on a mock
        verify(mockOne, never()).add("two");

        //verify that other mocks were not interacted
        verifyZeroInteractions(mockTwo, mockThree);

    }
    
    /**
     * 验证多余的方法调用（只在需要的时候使用）
     */
    @Test
    public void testRedundantInvocations() {
        
        //模拟一个List实例
        List<String> mockedList = mock(List.class);
        //using mocks
        mockedList.add("one");
        mockedList.add("two");

        verify(mockedList).add("one");
        verify(mockedList).add("two");

        //following verification will fail 
        verifyNoMoreInteractions(mockedList);

    }
    
    /**
     * 对方法的连续调用返回值进行模拟
     */
    @Test
    public void testConsecutiveCalls() {
        //模拟一个BookDAL实例
        BookDAL bookDAL = mock(BookDAL.class);
        when(bookDAL.getBookTitleByIsbn("ISBN-NO1")).thenThrow(new RuntimeException()).thenReturn("TITLE1");

        //First call: throws runtime exception:
        thrown.expect(RuntimeException.class);
        bookDAL.getBookTitleByIsbn("ISBN-NO1");

        //Second call: prints "TITLE1"
        assertEquals("TITLE1", bookDAL.getBookTitleByIsbn("ISBN-NO1"));

        //Any consecutive call: prints "TITLE1" as well (last stubbing wins). 
        assertEquals("TITLE1", bookDAL.getBookTitleByIsbn("ISBN-NO1"));
        
        when(bookDAL.getBookTitleByIsbn("ISBN-NO2")).thenReturn("TITLE2", "TITLE3", "TITLE4");

        assertEquals("TITLE2", bookDAL.getBookTitleByIsbn("ISBN-NO2"));
        assertEquals("TITLE3", bookDAL.getBookTitleByIsbn("ISBN-NO2"));
        assertEquals("TITLE4", bookDAL.getBookTitleByIsbn("ISBN-NO2"));

    }

    /**
     * 使用回调函数来模拟方法返回值（不常用）
     */
    @Test
    public void testStubWithCallback() {
        //模拟一个BookDAL实例
        BookDAL bookDAL = mock(BookDAL.class);
        
        when(bookDAL.getBookTitleByIsbn(anyString())).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                //Object mock = invocation.getMock();
                return "ISBN=" + args[0];
            }
        });

        //Following prints "called with arguments: foo"
        assertEquals("ISBN=foo", bookDAL.getBookTitleByIsbn("foo"));
    }
    
    /**
     * 在真实对象上做模拟，如果没有做模拟，那么会直接调用对象的直实方法
     */
    @Test
    public void testSpyRealObject() {
        
        List<String> list = new LinkedList<String>();
        List<String> spy = spy(list);

        //可以对个别的方法进行模拟
        when(spy.size()).thenReturn(100);

        //没有模拟的方法会直接调用直实方法
        spy.add("one");
        spy.add("two");

        //没有模拟的方法会返回真实数据
        System.out.println(spy.get(0));

        //做了模拟的方法会返回模拟的数据
        System.out.println(spy.size());

        //也可以对spy对象进行行为验证
        verify(spy).add("one");
        verify(spy).add("two");
        
        //有时候when方法对spy对象可能无法使用，此时可以考虑使用doXXX()方法
        
        List<String> listEmpty = new LinkedList<String>();
        List<String> spyListEmpty = spy(listEmpty);

        //如下spyListEmpty.get(0)会抛出IndexOutOfBoundsException
        thrown.expect(IndexOutOfBoundsException.class);
        when(spyListEmpty.get(0)).thenReturn("foo");

        //所以只能使用doReturn()进行数据模拟
        doReturn("foo").when(spyListEmpty).get(0);

    }
    
    /**
     * 部分模拟：
     * 1. 使用在真实对象上使用spy方法
     * 2. 在mock对象上使用thenCallRealMethod()或doCallRealMethod()
     */
    @Test
    public void testPartialMock() {

        //创建spy对象  
        List<String> list = spy(new LinkedList<String>());
        when(list.size()).thenReturn(100);
        list.add("one");
        assertEquals(100, list.size());

        //对mock对象调用对象的真实方法
        Book mock = mock(Book.class);
        //调用真实方法时必须确保真实方法的安全
        //如真实方法抛出或依赖对象的特定状态，那就会出现问题
        doCallRealMethod().when(mock).setTitle(anyString());

    }
    
    /**
     * 重置Mock对象，通常情况下不需要进行重置，如果需要，那么可以考虑是否测试过度
     */
    @Test
    public void testResetMock() {
        List<Integer> mock = mock(List.class);
        when(mock.size()).thenReturn(10);
        mock.add(1);

        reset(mock);
        assertEquals(0, mock.size());
    }
    
    /**
     * Aliases for behavior driven development
     */
    @Test
    public void testBDD() {
        Book book = mock(Book.class);
        
        //given  
        given(book.getTitle()).willReturn("BOOK_TITLE");
          
        //when
        String title = book.getTitle();
          
        //then
        assertEquals(title, "BOOK_TITLE");
    }
    
    /**
     * Mock可序列化的对象
     */
    @Test
    public void testMockSerializable() {
        //Mock
        List<String> serializableMock = mock(List.class, withSettings().serializable());
        assertNotNull(serializableMock);
        
        //Spy
        List<Object> list = new ArrayList<Object>();
        List<Object> spy = mock(ArrayList.class, withSettings()
                        .spiedInstance(list)
                        .defaultAnswer(CALLS_REAL_METHODS)
                        .serializable());
        assertNotNull(spy);
    }
    
    @Captor private ArgumentCaptor<Book> captor;
    
    @Spy private Book spyOnBook1 = new Book("TITLE"); //基于特定构造方法spy对象
    @Spy private Book spyOnBook2; //基于默认构造方法spy对象
    //不使用注解也可以，Mockito不能Mock内部类、匿名类、抽象类和接口
    //private Book spyOnBook3 = spy(new Book("TITLE"));
    //private Book spyOnBook4 = spy(new Book());
    
    /**
     * 使用@Mock注解来mock对象
     */
    @Mock(name = "book") private Book bookArg = new Book("FOR_INJECT");
    @InjectMocks private BookDAL bookDAL;

    //为了使用@Mock/@Spy/@InjectMocks注解生效，需要在@Before方法中调用
    // MockitoAnnotations.initMocks(this)
    //或在测试类上添加注解（JUnit4.4）
    // @RunWith( MockitoJUnitRunner.class )
    @Before
    public void init(){
       MockitoAnnotations.initMocks(this);
    }
    
    /**
     * Mockito的注解@Mock，@Spy，@InjectMocks
     * Mock注解用于mock一个对象
     * Spy注解用于spy一个对象
     * InjectMocks注解可以在测试类初始化时向其[构造方法]、[setter方法]、[成员变量]传递mock对象
     */
    @Test
    @Ignore
    public void testAnnotation() {
        BookDAL mocked1 = mock(BookDAL.class);
        mocked1.setBookTitle(new Book("Original Title"), "New Title");
        verify(mocked1).setBookTitle(captor.capture(), eq("New Title"));
        assertEquals("Original Title", captor.getValue().getTitle());
        
        this.bookDAL.changeSampleTitle();
        verify(bookArg).setTitle("INJECTED");
    }
    
    /**
     * 验证方法执行超时（待研究）
     */
    @Test
    public void testVerificationTimeout() {
        final Book mock = mock(Book.class);
        
        //在另外一个线程里调用mock对象的方法
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(20);
                    mock.timeoutMethod();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();;

        //passes when someMethod() is called within given time span 
        verify(mock, timeout(100)).timeoutMethod();
        //above is an alias to:
        verify(mock, timeout(100).times(1)).timeoutMethod();

        mock.timeoutMethod();

        //passes when someMethod() is called *exactly* 2 times within given time span
        verify(mock, timeout(100).times(2)).timeoutMethod();

        //passes when someMethod() is called *at least* 2 times within given time span
        verify(mock, timeout(100).atLeast(2)).timeoutMethod();

    }
    
    /**
     * 在一行代码里创建模拟对象并设置模拟数据
     */
    @Test
    public void testOneLineStub() {
        Book mock = when(mock(Book.class).getTitle()).thenReturn("TITLE_LINE").getMock();
        assertEquals("TITLE_LINE", mock.getTitle());
    }
    
    /**
     * 检查mock对象上是否有未验证的行为
     */
    @Test
    public void testIgnoreStub() {
        Book mock1 = mock(Book.class);
        Book mock2 = mock(Book.class);
        
        mock1.getTitle();
        mock2.getIsbn();
        
        verify(mock1).getTitle();
        verify(mock2).getIsbn();

        //ignores all stubbed methods:
        verifyNoMoreInteractions(ignoreStubs(mock1, mock2));

        //creates InOrder that will ignore stubbed
        InOrder inOrder = inOrder(ignoreStubs(mock1, mock2));
        inOrder.verify(mock1).getTitle();
        inOrder.verify(mock2).getIsbn();
        inOrder.verifyNoMoreInteractions();

    }
    
    /**
     * 检查对象是否为mock对象
     */
    @Test
    public void testMockDetail() {
        Book mock = mock(Book.class);
        Book book = new Book();
        Book spy = spy(book);
        
        assertTrue(Mockito.mockingDetails(mock).isMock());
        assertTrue(Mockito.mockingDetails(spy).isMock());
        assertTrue(Mockito.mockingDetails(spy).isSpy());
    }

}
