package me.cintia;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;


public class PdfWriter {

	public static void printHelp(){
		System.out.println("Correct usage: -f fileinput.html -o fileoutput.pdf");
	}


	public static void main(String[] args) throws Exception {
		OptionParser parserArgs = new OptionParser("f:u:o:h");

		OptionSet options = parserArgs.parse(args);

		if (args.length == 0 || options.has("h")){
			printHelp();
			return;
		}
		else if ( (!options.has("f") && !options.has("u")) || !options.has("o")){
			System.err.println("Usage error.");
			printHelp();
			return;
		}


		URL url = null;
		if (options.has("f")){
			String inputFile = (String) options.valueOf("f");
			url = new File(inputFile).toURI().toURL();
		} else {
			url = new URL((String) options.valueOf("u"));
		}

		String outputFile = (String) options.valueOf("o");
        OutputStream os = new FileOutputStream(outputFile);

        HTMLConfiguration nekoConfig = new HTMLConfiguration();
        nekoConfig.setFeature("http://xml.org/sax/features/namespaces", false);
        nekoConfig.setProperty("http://cyberneko.org/html/properties/names/attrs", "lower");
        nekoConfig.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");

        DOMParser parser = new DOMParser(nekoConfig);
        parser.parse(new InputSource(url.openStream()));
        Document doc = parser.getDocument();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(doc, url.toString());
        renderer.layout();
        renderer.createPDF(os);

        os.close();

	}

}
