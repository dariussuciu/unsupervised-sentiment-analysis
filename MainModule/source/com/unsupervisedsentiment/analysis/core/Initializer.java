package com.unsupervisedsentiment.analysis.core;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class Initializer {

	private static Config configInstance;

	public static Config getConfig() {
		if (configInstance == null) {
			JAXBContext jaxbContext;
			try {
				jaxbContext = JAXBContext.newInstance(Config.class);
				final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				return (Config) jaxbUnmarshaller.unmarshal(new File("config.xml"));
			} catch (JAXBException e) {
				e.printStackTrace();
				return null;
			}
		} else
			return configInstance;
	}

	public static void setConfig(Config config) {
		configInstance = config;
	}
}
