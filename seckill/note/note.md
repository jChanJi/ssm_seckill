# 控制反转
> 应用程序本身不负责依赖对象的 创建和维护，而是由外部容器创建和维护
>获得依赖的过程被反转了

# 面向切面 (Aspect Oriented Programming)
> 1. 是一种编程范式
> 2. 与语言无关，是一种编程范式
> 3.利用横切的技术，将面对对象的庞大的类进行水平的切割，并且将影响多个类的公共行为封装成可重用的模块。
> 4. 将通用的逻辑从业务逻辑中分离出来
> 5. 简单理解就是在执行一个方法或者类之前进行其他的拦截操作
> 6. spring容器启动的时候就已经实例化了对象，http请求之后不会再调用构造方法，所以进行类权限拦截的时候只能在每个类中添加判断，很不方便，这时候就可以用AOP

# restful接口
GET: /seckill/list
POST /seckill/{seckillId}/execution
DELETE /seckill/{id}/delete
POST 表示资源状态的转移 后面为 /资源/资源id/对资源的操作
DELETE 这里理解为名词
# post和put区别
PUT被定义为idempotent（幂等性）的方法，POST则不是
1、举个例子，在我们的支付系统中，一个api的功能是创建收款金额二维码，它和金额相关，每个用户可以有多个二维码，如果连续调用则会创建新的二维码，这个时候就用POST
2、还是那个例子，用户的账户二维码只和用户关联，而且是一一对应的关系，此时这个api就可以用PUT，因为每次调用它，都将刷新用户账户二维码
比如一个接口用于用户生成，接收的数据是用户名、密码等相关信息，则用PUT
# 幂等性
f(f(x)) = f(x)。x被函数f作用一次和作用无限次的结果是一样的。幂等性应用在软件系统中，我把它简单定义为：某个函数或者某个接口使用相同参数调用一次或者无限次，其造成的后果是一样的，

# pringmvc运行流程

1、用户->DispatcherServlet(中央控制器)，用户的所有请求都会映射到DispatcherServlet，他会拦截所欲的请求
2、DispatcherServlet->DefaultAnnotation HandlerMapping,用来映射URL,每一个URL
对应到每一个Handler
3、DefaultAnnotationHandlerAdapter用来做handler的适配，会衔接到SeckillController,如果使用拦截器也会将拦截器绑定到流程当中
4、ModelAndView(/seckill/list)，可以理解为list.jsp这个页面
5、交付到DispatcherServlet当中
6、判断使用的view为InternalResource ViewResolver(jsp的一个view)
7、将Model和list.jsp相结合
8、返回用户

#RequestMapper注解
1、？ 表示匹配一个字符，* 表示普配任意字符，** 表示匹配任意的URL路径
2、{xxxx}带占位符的URL
