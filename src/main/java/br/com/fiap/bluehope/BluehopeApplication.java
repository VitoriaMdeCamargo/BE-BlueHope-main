package br.com.fiap.bluehope;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@SpringBootApplication
@Controller
@EnableCaching
@OpenAPIDefinition(
	info = @Info(
		title = "BlueHope",
		version = "1.0",
		contact = @Contact(name = "Breno Santiago", email = "breno.santiago.8@gmail.com"),
		license = @License(name = "MIT", url = "https://opensource.org/license/mit"),
		summary = "API do app HeatWise"
	)
)
public class BluehopeApplication {

	public static void main(String[] args) {
		SpringApplication.run(BluehopeApplication.class, args);
	}

	@RequestMapping
	@ResponseBody 
	public String home(){
		return "BlueHope";
	}

}
