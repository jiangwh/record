# Zuul的工作机制

## Zuul in SpringBoot

```
一般情况我们在springboot工程中使用
@EnableZuulProxy
或者 
@EnableZuulServer
Set up the application to act as a generic Zuul server without any built-in reverse proxy features
两个注解中一个来启动zuul网关
还有一些配置文件，这里先忽略
```
## Being in EnableZuulServer
以EnableZuulServer为例看看zuul是如何工作的。
```
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ZuulConfiguration.class)
public @interface EnableZuulServer {

}
```
在EnableZuulServer注解中导入了ZuulConfiguration Class。
我们到ZuulConfiguration类中看下。
```
@Configuration
@EnableConfigurationProperties({ ZuulProperties.class })
@ConditionalOnClass(ZuulServlet.class)
只有classpath中存在ZuulServlet类才生效。
// Make sure to get the ServerProperties from the same place as a normal web app would
@Import(ServerPropertiesAutoConfiguration.class)
public class ZuulConfiguration {
...
}
```
我们看到ZuulConfiguration中定义了配置文件的加载，还没有看到运行器zuul网关的入口在哪里？继续往下看，在ZuulConfiguration中定义了一个bean  ZuulController。

```
	@Bean
	public ZuulController zuulController() {
		return new ZuulController();
	}
```
ZuulController这个具备迷惑性的名字，这不是我们正常使用spring web框架中Controller。我们看下细节：
```
public class ZuulController extends ServletWrappingController {

	public ZuulController() {
		setServletClass(ZuulServlet.class);
		setServletName("zuul");
		setSupportedMethods((String[]) null); // Allow all
	}
	...
}
```
ZuulController 继承了 ServletWrappingController，原来是把ZuulServlet包装成一个bean。
通过
```
public class ServletWrappingController extends AbstractController
		implements BeanNameAware, InitializingBean, DisposableBean {
		...
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.servletClass == null) {
			throw new IllegalArgumentException("'servletClass' is required");
		}
		if (this.servletName == null) {
			this.servletName = this.beanName;
		}
		this.servletInstance = this.servletClass.newInstance();
		this.servletInstance.init(new DelegatingServletConfig());
	}
	...
	}
```
通过```this.servletInstance.init(new DelegatingServletConfig());```来初始化ZuulServlet。
在ZuulController处理请求时，通过```super.handleRequestInternal(request, response);```将请求交给ZuulServlet处理。

## 小结
上面简单分析了springboot如何处理zuul的。其源码路径（父类的初始化不在这赘述了。）：

```

EnableZuulServer --> ZuulConfiguration -->ZuulController--> ZuulServlet
```


