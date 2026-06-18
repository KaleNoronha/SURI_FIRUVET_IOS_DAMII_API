Página
Número de página
1
de 7
Laboratorio 6-1. Servicio SOAP
Para que sea un escenario real, haremos ambas partes en Spring Boot 3.x usando Java 17+:
1. El Servidor (Producer): Creará y expondrá el servicio SOAP.
2. El Cliente (Consumer): Consumirá ese servicio SOAP externo.
   Seguiremos el enfoque Contract-First (Primero el Contrato), que es el estándar de la industria.
   PARTE 1: Crear el Servidor SOAP (Producer)
   Este servicio recibirá el ID de un producto y devolverá su nombre, precio y stock.
   Crearemos un proyecto con el nombre sugerido “server-soap” con esta estructura:
   1.1. Configurar el pom.xml
   Crea un proyecto Spring Boot con la dependencia de Web Services y el plugin jaxb2 para
   generar código Java desde el archivo XSD.
   XML
   <dependencies>
   <dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-web-services</artifactId>
   </dependency>
   <dependency>
   <groupId>wsdl4j</groupId>
   <artifactId>wsdl4j</artifactId>
   </dependency>
   </dependencies>
   <build>
   <plugins>
   <plugin>
   <groupId>org.codehaus.mojo</groupId>
   <artifactId>jaxb2-maven-plugin</artifactId>
   <version>3.1.0</version>
   <executions>
   <execution>
   <id>xjc</id>
   <goals><goal>xjc</goal></goals>
   </execution>
   </executions>
   <configuration>
   <sources>
<source>src/main/resources/xsd</source>
</sources>
<outputDirectory>src/main/java</outputDirectory>
<clearOutputDir>false</clearOutputDir>
</configuration>
</plugin>
</plugins>
</build>
1.2. Definir el Esquema XSD (src/main/resources/xsd/productos.xsd)
El contrato que define las reglas de nuestro XML.
XML
<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
xmlns:tns="http://tienda.com/webservices"
targetNamespace="http://tienda.com/webservices"
elementFormDefault="qualified">
<xs:element name="getProductoRequest">
<xs:complexType>
<xs:sequence>
<xs:element name="id" type="xs:int"/>
</xs:sequence>
</xs:complexType>
</xs:element>
<xs:element name="getProductoResponse">
<xs:complexType>
<xs:sequence>
<xs:element name="producto" type="tns:producto"/>
</xs:sequence>
</xs:complexType>
</xs:element>
<xs:complexType name="producto">
<xs:sequence>
<xs:element name="id" type="xs:int"/>
<xs:element name="nombre" type="xs:string"/>
<xs:element name="precio" type="xs:double"/>
<xs:element name="stock" type="xs:int"/>
</xs:sequence>
</xs:complexType>
</xs:schema>
¡Importante! Compila tu proyecto ahora (mvn compile o dale Reload a Maven en tu IDE). Esto
generará automáticamente un paquete dentro de src/main/java con las clases
GetProductoRequest, GetProductoResponse y Producto.
1.3. Configuración de Spring WS (WebServiceConfig.java)
Configuramos el servlet que escuchará las peticiones y generará el WSDL automáticamente.
Java
package com.tienda.config;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
@Bean
public ServletRegistrationBean<MessageDispatcherServlet>
messageDispatcherServlet(ApplicationContext applicationContext) {
MessageDispatcherServlet servlet = new MessageDispatcherServlet();
servlet.setApplicationContext(applicationContext);
servlet.setTransformWsdlLocations(true);
return new ServletRegistrationBean<>(servlet, "/ws/*");
}
@Bean(name = "productos") // Generará: /ws/productos.wsdl
public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema productosSchema) {
DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
wsdl11Definition.setPortTypeName("ProductosPort");
wsdl11Definition.setLocationUri("/ws");
wsdl11Definition.setTargetNamespace("http://tienda.com/webservices");
wsdl11Definition.setSchema(productosSchema);
return wsdl11Definition;
}
@Bean
public XsdSchema productosSchema() {
return new SimpleXsdSchema(new ClassPathResource("xsd/productos.xsd"));
}
}
1.4. Crear el Endpoint (ProductoEndpoint.java)
Aquí recibimos el XML mapeado a objeto Java y respondemos.
Java
package com.tienda.endpoint;
import com.tienda.webservices.GetProductoRequest;
import com.tienda.webservices.GetProductoResponse;
import com.tienda.webservices.Producto;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
@Endpoint
public class ProductoEndpoint {
private static final String NAMESPACE_URI = "http://tienda.com/webservices";
@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductoRequest")
@ResponsePayload
public GetProductoResponse getProducto(@RequestPayload GetProductoRequest request) {
GetProductoResponse response = new GetProductoResponse();
// Simulación de Base de Datos
Producto producto = new Producto();
producto.setId(request.getId());
if (request.getId() == 1) {
producto.setNombre("Laptop Gamer");
producto.setPrecio(1200.50);
producto.setStock(15);
} else {
producto.setNombre("Teclado Mecánico");
producto.setPrecio(85.00);
producto.setStock(50);
}
response.setProducto(producto);
return response;
}
}
Levanta esta aplicación (se ejecutará por defecto en el puerto 8080). Puedes verificar que
funcione entrando a: http://localhost:8080/ws/productos.wsdl.
Resultado Server:
PARTE 2: Crear el Cliente SOAP (Consumer)
Ahora crearemos una segunda aplicación de Spring Boot independiente (cámbiale el puerto
en application.properties a server.port=8081) que consumirá el WSDL del Servidor.
2.1. Configurar el pom.xml del Cliente
El cliente requiere las mismas dependencias de Web Services, pero usaremos el plugin para
leer el WSDL del servidor y generarnos el cliente automáticamente.
Puede crear un proyecto con el nombre “cliente-soap”.
XML
<dependencies>
<dependency>
<groupId>org.springframework.boot</groupId>
<artifactId>spring-boot-starter-web-services</artifactId>
</dependency>
</dependencies>
<build>
<plugins>
<plugin>
<groupId>org.codehaus.mojo</groupId>
<artifactId>jaxb2-maven-plugin</artifactId>
<version>3.1.0</version>
<executions>
<execution>
<id>wsdl-extract</id>
<goals><goal>xjc</goal></goals>
</execution>
</executions>
<configuration>
<sources>
<source>http://localhost:8080/ws/productos.wsdl</source>
</sources>
<outputDirectory>src/main/java</outputDirectory>
<clearOutputDir>false</clearOutputDir>
</configuration>
</plugin>
</plugins>
</build>
Nota: Asegúrate de tener corriendo el Servidor de la Parte 1 y ejecuta mvn compile en este
proyecto Cliente para que se generen las clases correspondientes.
2.2. Configurar el Cliente SOAP (SoapClientConfig.java)
Configuramos el componente WebServiceTemplate de Spring, que se encarga de serializar y
enviar los objetos por el protocolo SOAP.
Java
package com.example.cliente_soap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
@Configuration
public class SoapClientConfig {
@Bean
public Jaxb2Marshaller marshaller() {
Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
// El paquete donde se generaron las clases del WSDL
marshaller.setContextPath("com.tienda.webservices");
return marshaller;
}
@Bean
public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
WebServiceTemplate template = new WebServiceTemplate();
template.setMarshaller(marshaller);
template.setUnmarshaller(marshaller);
template.setDefaultUri("http://localhost:8080/ws"); // URL del servidor SOAP
return template;
}
}
2.3. Crear el Servicio de Consumo (ProductoClient.java)
Esta clase ejecutará la llamada SOAP usando el WebServiceTemplate.
Java
package com.example.cliente_soap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import com.tienda.webservices.GetProductoRequest;
import com.tienda.webservices.GetProductoResponse;
@Service
public class ProductoClient {
@Autowired
private WebServiceTemplate webServiceTemplate;
public GetProductoResponse obtenerProducto(int id) {
GetProductoRequest request = new GetProductoRequest();
request.setId(id);
// Envía el Request y parsea automáticamente el Response XML a objeto Java
return (GetProductoResponse) webServiceTemplate.marshalSendAndReceive(request);
}
}
2.4. Exponer un controlador REST para probar el flujo (ClienteController.java)
Para ver que todo funcione, crearemos un endpoint REST común y corriente en el cliente, que
por dentro llamará al servicio SOAP.
Java
package com.example.cliente_soap;
import com.tienda.webservices.GetProductoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class ClienteController {
@Autowired
private ProductoClient productoClient;
@GetMapping("/buscar-producto/{id}")
public GetProductoResponse testSoap(@PathVariable int id) {
return productoClient.obtenerProducto(id);
}
}
Prueba Final de Integración
1. Asegúrate de tener el Servidor corriendo (Puerto 8080).
2. Levanta el Cliente (Puerto 8081).
3. Abre tu navegador o Postman y haz una petición GET ordinaria a tu cliente:
http://localhost:8081/buscar-producto/1
Resultado: El Cliente (8081) armará un sobre XML SOAP por debajo, llamará al Servidor (8080),
este responderá con otro XML, el cliente lo parseará a Java y te lo mostrará en tu navegador
formateado limpiamente en JSON.