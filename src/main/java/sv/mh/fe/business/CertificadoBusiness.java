package sv.mh.fe.business;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import sv.mh.fe.constantes.Constantes;
import sv.mh.fe.filter.FirmarDocumentoFilter;
import sv.mh.fe.models.CertificadoMH;
import sv.mh.fe.security.Cryptographic;
import sv.mh.fe.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.springframework.core.io.ClassPathResource;

@Service
public class CertificadoBusiness {
	
	@Autowired
	private Cryptographic cryptographic;
	
	@Autowired
	private FileUtils fileUtilis;
	
	private static Logger logger = LoggerFactory.getLogger(CertificadoBusiness.class);		
	
	public CertificadoMH recuperarCertifiado(FirmarDocumentoFilter filter) throws IOException, NoSuchAlgorithmException {		
		XmlMapper xmlMapper = new XmlMapper();
		JavaTimeModule module = new JavaTimeModule();
		xmlMapper.registerModule(module);

		CertificadoMH certificado = null;
		String crypto = cryptographic.encrypt(filter.getPasswordPri(), Cryptographic.SHA512);
		
		//Path path = Paths.get(Constantes.DIRECTORY_UPLOADS,filter.getNit()+".crt");
		//Path path = Paths.get(Constantes.DIRECTORY_UPLOADS,filter.getNit()+".crt");
		// Ruta del archivo en src/main/resources/uploads
   		 String nombreArchivo = "uploads/06140912201056.crt"; // Usa el NIT desde el filtro

			//Path path = Paths.get("/uploads", nombreArchivo);
			try (InputStream inputStream = new ClassPathResource(nombreArchivo).getInputStream()) {
			// Leer contenido del archivo
			String contenido = new String(inputStream.readAllBytes());
			certificado = xmlMapper.readValue(contenido, CertificadoMH.class);
			} catch (IOException e) {
				logger.error("El archivo no se encontró: " + nombreArchivo, e);
				return null; // Manejar el caso donde el archivo no existe
			}

		/*
		String contenido = fileUtilis.LeerArchivo(path);
		certificado = xmlMapper.readValue(contenido, CertificadoMH.class);
		
		if(certificado.getPrivateKey().getClave().equals(crypto)){
			return certificado;			
		}
		logger.info("Password no valido: "+certificado.getNit());
		return null;
		*/


    if (certificado.getPrivateKey().getClave().equals(crypto)) {
        return certificado;            
    }
    logger.info("Password no válido: " + certificado.getNit());
    return null;

		
	}
}
