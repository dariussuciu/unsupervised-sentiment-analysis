package com.unsupervisedsentiment.analysis.core;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import com.unsupervisedsentiment.analysis.modules.IO.Input.Models.Config;

public class Initializer {

	public static Config getConfig() {
		JAXBContext jaxbContext;
		try 
		{
			jaxbContext = JAXBContext.newInstance(Config.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			return (Config) jaxbUnmarshaller.unmarshal(new File("config.xml"));
		}
		catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}
}
