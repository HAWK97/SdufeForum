###@Configuration
定义：  
```$xslt
@Target(value=TYPE)
@Retention(value=RUNTIME)
@Documented
@Component
public @interface Configuration
```
用法：  
修饰类，表明该类声明了  
  
###@EnableWebMvc
使用@EnableWebMvc注解等于继承WebMvcConfigurationSupport但是没有重写任何方法。
所以有以下几种使用方式：
* @EnableWebMvc+implements WebMvcConfigurer：这种方式会屏蔽Spring Boot的@EnableAutoConfiguration中的设置
* extends WebMvcConfigurationSupport：这种方式会屏蔽Spring Boot的@EnableAutoConfiguration中的设置
* implements WebMvcConfigurer：这种方式依旧使用Spring Boot的@EnableAutoConfiguration中的设置  

如果Spring Boot提供的Spring MVC配置不符合要求，可以通过一个配置类（注解有@Configuration的类）加上@EnableWebMvc注解来实现自定义的Spring MVC配置。
通常情况下，可以定义一个配置类并实现WebMvcConfigurer，无需使用@EnableWebMvc注解。
  
###@Autowired、@Resource与@Inject
