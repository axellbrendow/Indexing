/* See the project's root for license information. */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Classe utilitária para manusear os registros de índices em um bucket.
 * Um registro é apenas uma tripla onde o primeiro elemento é a lápide,
 * o segundo a chave e o terceiro o dado: { lapide, chave, dado }.
 * 
 * @author Axell Brendow ( https://github.com/axell-brendow )
 *
 * @param <TIPO_DAS_CHAVES> Classe da chave. Caso essa classe não seja de um
 * tipo primitivo, ela deve ser filha da interface {@link Serializavel}.
 * @param <TIPO_DOS_DADOS> Classe do dado. Caso essa classe não seja de um
 * tipo primitivo, ela deve ser filha da interface {@link Serializavel}.
 */

public class RegistroDoIndice<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> implements Serializavel
{
	public static final char REGISTRO_ATIVADO = ' ';
	public static final char REGISTRO_DESATIVADO = '*';
	public static int TAMANHO_MAXIMO_EM_BYTES_STRINGS = 300;
	
	protected char lapide;
	protected TIPO_DAS_CHAVES chave;
	protected TIPO_DOS_DADOS dado;
	protected short quantidadeMaximaDeBytesParaAChave;
	protected short quantidadeMaximaDeBytesParaODado;
	protected Class<TIPO_DAS_CHAVES> classeDaChave;
	protected Class<TIPO_DOS_DADOS> classeDoDado;
	protected Constructor<TIPO_DAS_CHAVES> construtorDaChave;
	protected Constructor<TIPO_DOS_DADOS> construtorDoDado;

	/**
	 * Dada uma classe, caso ela seja de um tipo primitivo, obtém a quantidade de
	 * bytes que esse primitivo gasta no Java. Caso ela seja de um tipo
	 * {@link Serializavel}, obtém a quantidade máxima de bytes que esse
	 * {@link Serializavel} gasta. Caso contrário, imprime um erro indicando que
	 * a classe precisa ser de um tipo primitivo ou {@link Serializavel}.
	 *
	 * @param classe Classe do objeto que deseja-se obter a quantidade de bytes.
	 *
	 * @return a quantidade máxima de bytes que objetos dessa classe gastam.
	 */
	private short obterTamanhoMaximoEmBytes(Class<?> classe)
	{
		int tamanho = -1;

		if (classe.equals(String.class)) tamanho = TAMANHO_MAXIMO_EM_BYTES_STRINGS;

		else if (Classes.isWrapper(classe))
		{
			if (classe.equals(Boolean.class)) tamanho = 1;
			else if (classe.equals(Character.class)) tamanho = Character.BYTES;
			else if (classe.equals(Byte.class)) tamanho = Byte.BYTES;
			else if (classe.equals(Short.class)) tamanho = Short.BYTES;
			else if (classe.equals(Integer.class)) tamanho = Integer.BYTES;
			else if (classe.equals(Long.class)) tamanho = Long.BYTES;
			else if (classe.equals(Float.class)) tamanho = Float.BYTES;
			else if (classe.equals(Double.class)) tamanho = Double.BYTES;
			else if (classe.equals(Void.class))
				IO.printlnerr("ERRO: a classe " + classe.getName() + " é do tipo VOID.");
			else
				IO.printlnerr("ERRO: a classe " + classe.getName() + " é inválida.");
		}

		else if (Serializavel.class.isAssignableFrom(classe)) // Checa se a classe é Serializavel
		{
			try
			{ tamanho = ( (Serializavel) classe.newInstance() ).obterTamanhoMaximoEmBytes(); }

			catch (InstantiationException | IllegalAccessException e) { e.printStackTrace(); }
		}

		else IO.printlnerr("ERRO: a classe " + classe.getName() +
					" não é de tipo primitivo nem implementa a interface Serializavel.");

		return (short) tamanho;
	}
	
	/**
	 * Cria um objeto que gerencia um registro de indice no bucket da hash dinâmica.
	 * 
	 * @param lapide Lapide do registro.
	 * @param chave Chave do registro.
	 * @param dado Dado que corresponde à chave.
	 * @param classeDaChave Construtor da chave. É necessário que a chave tenha um
	 * construtor sem parâmetros.
	 * @param classeDoDado Construtor do dado. É necessário que o dado tenha um
	 * construtor sem parâmetros.
	 */
	
	public RegistroDoIndice(
		char lapide,
		TIPO_DAS_CHAVES chave,
		TIPO_DOS_DADOS dado,
		Class<TIPO_DAS_CHAVES> classeDaChave,
		Class<TIPO_DOS_DADOS> classeDoDado)
	{
		this.lapide = lapide;
		this.chave = chave;
		this.dado = dado;
		this.classeDaChave = classeDaChave;
		this.classeDoDado = classeDoDado;

		try
		{
			this.construtorDaChave = Classes.isWrapper(classeDaChave) ?
				classeDaChave.getConstructor( Classes.wrapperParaPrimitivo.get(classeDaChave) ) :
				classeDaChave.getConstructor();

			this.construtorDoDado = Classes.isWrapper(classeDoDado) ?
				classeDoDado.getConstructor( Classes.wrapperParaPrimitivo.get(classeDoDado) ) :
				classeDoDado.getConstructor();

			this.quantidadeMaximaDeBytesParaAChave = obterTamanhoMaximoEmBytes(classeDoDado);
			this.quantidadeMaximaDeBytesParaODado = obterTamanhoMaximoEmBytes(classeDaChave);
		}

		catch (NoSuchMethodException | SecurityException | IllegalArgumentException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Calcula o tamanho que cada registro de indice gasta no bucket.
	 * 
	 * @return o tamanho que cada registro de indice gasta no bucket.
	 */
	
	@Override
	public int obterTamanhoMaximoEmBytes()
	{
		int tamanho = 0;
		
		if (quantidadeMaximaDeBytesParaAChave > 0 &&
			quantidadeMaximaDeBytesParaODado > 0)
		{
			tamanho = Byte.BYTES + // tamanho da lapide
				quantidadeMaximaDeBytesParaAChave + // tamanho da chave
				quantidadeMaximaDeBytesParaODado; // tamanho do dado
		}
		
		return tamanho;
	}
	
	/**
	 * Preenche com zeros a saída de dados até que o espaço
	 * usado seja igual ao tamanho máximo.
	 * 
	 * @param dataOutputStream Corrente de saída de dados.
	 * @param tamanhoUsado Quantidade de bytes já escritos.
	 * @param tamanhoMaximo Tamanho máximo desejado.
	 */
	
	private void completarEspacoNaoUsado(
		DataOutputStream dataOutputStream,
		int tamanhoUsado,
		int tamanhoMaximo)
	{
		int restante = tamanhoMaximo - tamanhoUsado;
		
		if (restante > 0)
		{
			byte[] lixo = new byte[restante];
			
			Arrays.fill(lixo, (byte) 0);
			
			try
			{
				dataOutputStream.write(lixo);
			}
			
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Obtém um arranjo com os bytes do objeto.
	 *
	 * @param objeto Objeto serializável.
	 * @param classe Classe do objeto.
	 *
	 * @return um arranjo com os bytes do objeto.
	 */
	private byte[] obterBytes(Object objeto, Class<?> classe)
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

		try
		{
			if (classe.equals(String.class)) dataOutputStream.writeUTF((String) objeto);

			else if (Classes.isWrapper(classe))
			{
				if (classe.equals(Boolean.class)) dataOutputStream.writeBoolean((Boolean) objeto);
				else if (classe.equals(Character.class)) dataOutputStream.writeChar((Character) objeto);
				else if (classe.equals(Byte.class)) dataOutputStream.writeByte((Byte) objeto);
				else if (classe.equals(Short.class)) dataOutputStream.writeShort((Short) objeto);
				else if (classe.equals(Integer.class)) dataOutputStream.writeInt((Integer) objeto);
				else if (classe.equals(Long.class)) dataOutputStream.writeLong((Long) objeto);
				else if (classe.equals(Float.class)) dataOutputStream.writeFloat((Float) objeto);
				else if (classe.equals(Double.class)) dataOutputStream.writeDouble((Double) objeto);
				else if (classe.equals(Void.class))
					IO.printlnerr("ERRO: a classe " + classe.getName() + " é do tipo VOID.");
				else
					IO.printlnerr("ERRO: a classe " + classe.getName() + " é inválida.");
			}

			// Checa se a classe é Serializavel
			else if (Serializavel.class.isAssignableFrom(classe))
				dataOutputStream.write( ((Serializavel) classe.newInstance()).obterBytes() );

			else IO.printlnerr("ERRO: a classe " + classe.getName() +
					" não é de tipo primitivo nem implementa a interface Serializavel.");
		}

		catch (IOException | InstantiationException | IllegalAccessException e)
		{ e.printStackTrace(); }

		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public byte[] obterBytes()
	{
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		
		try
		{
			dataOutputStream.writeByte(lapide);
			
			byte[] byteArrayChave = obterBytes(chave, classeDaChave);
			
			dataOutputStream.write(byteArrayChave);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayChave.length,
				quantidadeMaximaDeBytesParaAChave
			);
			
			byte[] byteArrayDado = obterBytes(dado, classeDoDado);
			
			dataOutputStream.write(byteArrayDado);
			
			completarEspacoNaoUsado(
				dataOutputStream,
				byteArrayDado.length,
				quantidadeMaximaDeBytesParaODado
			);
			
			dataOutputStream.close();
		}
		
		catch (IOException ioex)
		{
			ioex.printStackTrace();
		}
		
		return byteArrayOutputStream.toByteArray();
	}

//	/**
//	 * Atribui o campo "value" do objeto primitivo para o valor informado.
//	 * Este método precisa também da classe do objeto primitivo.
//	 *
//	 * @param value Valor a ser atribuído ao campo.
//	 * @param primitive Objeto de tipo primitivo que o campo será alterado.
//	 * @param classe Classe desse tipo primitivo.
//	 */
//	private void setTo(Object value, Object primitive, Class<?> classe)
//	{
//		try
//		{
//			// Pega o campo "value" da classe. É esperado que essa classe seja
//			// de um tipo primitivo pois todos eles têm o campo "value"
//			Field field = classe.getDeclaredField("value");
//			boolean acessibility = field.isAccessible();
//
//			// Se o campo é private ou protected, deixa-o acessível
//			field.setAccessible(true);
//			field.set(primitive, value); // Atribui o campo para o valor
//			field.setAccessible(acessibility); // Volta a acessibilidade
//		}
//
//		catch (NoSuchFieldException | IllegalAccessException e)
//		{
//			e.printStackTrace();
//		}
//	}

	/**
	 * Faz com que o objeto leia o arranjo de bytes restaurando o seu estado.
	 *
	 * @param array Arranjo com os bytes do objeto.
	 * @param nomeCampo Nome do campo desta classe que contém o objeto.
	 * @param classe Classe do objeto.
	 */
	private void lerBytes(byte[] array, String nomeCampo, Class<?> classe)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		Field field;
		boolean accessibility;

		try
		{
			field = getClass().getDeclaredField(nomeCampo);

			accessibility = field.isAccessible();
			field.setAccessible(true);

			if (classe.equals(String.class)) field.set(this, dataInputStream.readUTF());

			else if (Classes.isWrapper(classe))
			{
				if (classe.equals(Boolean.class))
					field.set(this, new Boolean( dataInputStream.readBoolean() ));

				else if (classe.equals(Character.class))
					field.set(this, new Character( dataInputStream.readChar() ));

				else if (classe.equals(Byte.class))
					field.set(this, new Byte( dataInputStream.readByte() ));

				else if (classe.equals(Short.class))
					field.set(this, new Short( dataInputStream.readShort() ));

				else if (classe.equals(Integer.class))
					field.set(this, new Integer( dataInputStream.readInt() ));

				else if (classe.equals(Long.class))
					field.set(this, new Long( dataInputStream.readLong() ));

				else if (classe.equals(Float.class))
					field.set(this, new Float( dataInputStream.readFloat() ));

				else if (classe.equals(Double.class))
					field.set(this, new Double( dataInputStream.readDouble() ));

				else if (classe.equals(Void.class))
					IO.printlnerr("ERRO: a classe " + classe.getName() +
						" é do tipo VOID.");

				else
					IO.printlnerr("ERRO: a classe " + classe.getName() +
						" é inválida.");
			}

			// Checa se a classe é Serializavel
			else if (Serializavel.class.isAssignableFrom(classe))
				((Serializavel) field.get(this)).lerBytes(array);

			else IO.printlnerr("ERRO: a classe " + classe.getName() +
					" não é de tipo primitivo nem implementa a interface Serializavel.");

			field.setAccessible(accessibility);
		}

		catch (NoSuchFieldException | IOException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void newInstance(String nomeCampo, Class<?> classe)
	{
		Field field;
		boolean accessibility;

		try
		{
			field = getClass().getDeclaredField(nomeCampo);
			accessibility = field.isAccessible();
			field.setAccessible(true);

			if (classe.equals(String.class)) field.set(this, "");

			else if (Classes.isWrapper(classe))
			{
				if (classe.equals(Boolean.class)) field.set(this, false);
				else if (classe.equals(Character.class)) field.set(this, 0);
				else if (classe.equals(Byte.class)) field.set(this, 0);
				else if (classe.equals(Short.class)) field.set(this, 0);
				else if (classe.equals(Integer.class)) field.set(this, 0);
				else if (classe.equals(Long.class)) field.set(this, 0);
				else if (classe.equals(Float.class)) field.set(this, 0);
				else if (classe.equals(Double.class)) field.set(this, 0);
				else if (classe.equals(Void.class))
					IO.printlnerr("ERRO: a classe " + classe.getName() +
						" é do tipo VOID.");
				else
					IO.printlnerr("ERRO: a classe " + classe.getName() +
						" é inválida.");
			}

			// Checa se a classe é Serializavel
			else if (Serializavel.class.isAssignableFrom(classe))
				field.set(this, classe.newInstance());

			else IO.printlnerr("ERRO: a classe " + classe.getName() +
					" não é de tipo primitivo nem implementa a interface Serializavel.");

			field.setAccessible(accessibility);
		}

		catch (NoSuchFieldException | IllegalAccessException | InstantiationException e)
		{ e.printStackTrace(); }
	}

	@Override
	public void lerBytes(byte[] bytes)
	{
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
		
		try
		{
			lapide = (char) dataInputStream.readByte();
			
			byte[] byteArrayChave = new byte[quantidadeMaximaDeBytesParaAChave];
			byte[] byteArrayDado = new byte[quantidadeMaximaDeBytesParaODado];
			
			dataInputStream.read(byteArrayChave);
			dataInputStream.read(byteArrayDado);

			newInstance("chave", classeDaChave);
			newInstance("dado", classeDoDado);
//			chave = construtorDaChave.newInstance();
//			dado = construtorDoDado.newInstance();

			lerBytes(byteArrayChave, "chave", classeDaChave);
			lerBytes(byteArrayDado, "dado", classeDoDado);
			
			byteArrayInputStream.close();
			dataInputStream.close();
		}

		catch (IllegalArgumentException |
				IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public String toString(String delimitadorEntreOsCamposDoRegistro, boolean mostrarApenasAChave)
	{
		return mostrarApenasAChave ? chave.toString() :
			(
    			"'" + lapide + "'" + delimitadorEntreOsCamposDoRegistro +
    			chave + delimitadorEntreOsCamposDoRegistro +
    			dado
			);
	}
	
	@Override
	public String toString()
	{
		return toString(", ", false);
	}
}
