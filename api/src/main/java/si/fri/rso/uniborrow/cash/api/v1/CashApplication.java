package si.fri.rso.uniborrow.cash.api.v1;

import com.kumuluz.ee.discovery.annotations.RegisterService;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "Uniborrow Cash API",
                version = "v1",
                contact = @Contact(email = "ks0466@student.uni-lj.si"),
                license = @License(name = "dev"),
                description = "API for managing cash for Uniborrow application."
        ),
        servers = @Server(url = "http://35.223.79.242/uniborrow-cash/")
)
@RegisterService
@ApplicationPath("/v1")
public class CashApplication extends Application {

}
