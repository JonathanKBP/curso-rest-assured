package br.ce.jonathankbp.rest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.XmlPath.CompatibilityMode;

public class AuthTest {

	@Test
	public void deveAcessarSWAPI() {
		given()
			.log().all()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"));
	}
	
	@Test
	public void deveObterClima() {
		given()
			.log().all()
			.queryParam("q", "Fortaleza,BR")
			.queryParam("appid", "6a008edace200193a30754d730d98bfd")
		.when()
			.get("https://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Fortaleza"))
			//.body("coord.lon", is(-38.52f))
			.body("main.temp", greaterThan(25f));
	}
	
	@Test
	public void naoDeveAcessarSemSenha() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401);
	}
	
	@Test
	public void DevefazerAutenticacaoBasica() {
		given()
			.log().all()
		.when()
			.get("https://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"));
	}
	
	@Test
	public void DevefazerAutenticacaoBasica2() {
		given()
			.log().all()
			.auth().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"));
	}
	
	@Test
	public void DevefazerAutenticacaoBasicaChallenge() {
		given()
			.log().all()
			.auth().preemptive().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"));
	}
	
	@Test
	public void DevefazerAutenticacaoComTokenJWT() {
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "jonathan@gmail");
		login.put("senha", "123456");
		
		String token = given()
			.log().all()
			.body(login)
			.contentType(ContentType.JSON)
		.when()
			.post("https://barrigarest.wcaquino.me/signin")
		.then()
			.log().all()
			.statusCode(200)
			.extract().path("token");
		
		given()
			.log().all()
			.header("Authorization", "JWT " + token)
		.when()
			.get("https://barrigarest.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("nome", hasItem("teste"));
	}
	
	@Test
	public void deveAcessarAplicacaoWeb() {
		String cookie = given()
			.log().all()
			.formParam("email", "jonathan@gmail")
			.formParam("senha", "123456")
			.contentType(ContentType.URLENC.withCharset("UTF-8"))
		.when()
			.post("http://seubarriga.wcaquino.me/logar")
		.then()
			.log().all()
			.statusCode(200)
			.extract().header("set-cookie");
		
		cookie = cookie.split("=")[1].split(";")[0];
		
		String body = given()
			.log().all()
			.cookie("connect.sid", cookie)
		.when()
			.get("http://seubarriga.wcaquino.me/contas")
		.then()
			.log().all()
			.statusCode(200)
			.body("html.body.table.tbody.tr[0].td[0]", is("teste"))
			.extract().body().asString();
		
		XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, body);
		System.out.print(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
		
	}
}



