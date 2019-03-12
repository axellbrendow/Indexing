import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public interface Serializavel
{
	void escreverObjeto(ObjectOutputStream correnteDeSaida);
	void lerObjeto(ObjectInputStream correnteDeEntrada);
	byte[] obterBytes();
	void lerBytes(byte[] bytes);
}
