package com.unsupervisedsentiment.analysis.modules.IO;

public class FileWrapper {

	private String author;
	private String source;
	private String content;
	private String filename;

	public FileWrapper() {
	}

	public FileWrapper(String author, String source, String content,
			String filename) {
		super();
		this.author = author;
		this.source = source;
		this.content = content;
		this.filename = filename;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	/**
	 * 
	 * @return the un-annotated text
	 */
	public String getOriginalContent() {
		String cleanContent = content.replaceAll("(###)|(%%%)|(@@@)|(\\$\\$\\$)", "");
		return cleanContent;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
