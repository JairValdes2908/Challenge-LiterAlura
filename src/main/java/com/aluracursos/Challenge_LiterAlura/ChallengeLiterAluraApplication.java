package com.aluracursos.Challenge_LiterAlura;

import com.aluracursos.Challenge_LiterAlura.principal.Principal;
import com.aluracursos.Challenge_LiterAlura.repository.AutorRepository;
import com.aluracursos.Challenge_LiterAlura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengeLiterAluraApplication implements CommandLineRunner {
	@Autowired
	LibroRepository libroRepository;
	@Autowired
	AutorRepository autorRepository;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeLiterAluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(libroRepository, autorRepository);
		principal.mostrarMenu();
	}
}

