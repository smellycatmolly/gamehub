package com.gamehub.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//		System.out.println(passwordEncoder.encode("pmzy"));
		System.out.println(passwordEncoder.matches("p2mzy", "$2a$10$bezfPK4Jhu45focQq/msF.c2lFANllpnF00apMSE29oqryMi5g26O"));

	}

}
