package com.citrix.interview;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/documents/*")
public class DocumentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final String dir = "C://Users//Hrishikesh//workspace//docstorage//src//logmein//";

	class Document {
		public String name;
		public String id;

		public Document(String _id, String _name) {
			name = _name;
			id = _id;
		}
	}

	private Gson _gson = null;
	private HashMap<String, Document> _documents = new HashMap<>();

	public DocumentServlet() {
		super();

		_gson = new Gson();

	}

	private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {

		response.setContentType("application/json");

		String res = _gson.toJson(obj);

		PrintWriter out = response.getWriter();

		out.print(res);
		out.flush();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			String id1 = UUID.randomUUID().toString();
			String id2 = UUID.randomUUID().toString();
			String id3 = UUID.randomUUID().toString();
			
			if (_documents.size() == 0) {
			_documents.put(id1, new Document(id1, "Text.txt"));
			_documents.put(id2, new Document(id2, "image.jpeg"));
			_documents.put(id3, new Document(id3, "resume.pdf"));

			try {

				File file1 = new File(dir + _documents.get(id1).name);
				File file2 = new File(dir + _documents.get(id2).name);
				File file3 = new File(dir + _documents.get(id3).name);

				if (file1.createNewFile()) {

				} else {
					_documents.remove(id1);
				}
				if (file2.createNewFile()) {

				} else {
					_documents.remove(id2);
				}
				if (file3.createNewFile()) {

				} else {
					_documents.remove(id3);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			HashMap<Integer, String> files = new HashMap<Integer, String>();
			File directory = new File(dir);
			File[] fList = directory.listFiles();
			
				for (File file : fList) {
					if (file.isFile()) {
						String id4 = UUID.randomUUID().toString();
						_documents.put(id4, new Document(id4, file.getName()));
					}
				}
			}
			Collection<Document> doc = _documents.values();
			sendAsJson(response, doc);
			return;

		}

		String[] splits = pathInfo.split("/");
		if (splits.length != 2) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String docId = splits[1];
		if (_documents.containsKey(docId)) {

			sendAsJson(response, _documents.get(docId));
			return;
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String[] splits = pathInfo.split("/");
		String DocName = splits[1];
		String id1 = UUID.randomUUID().toString();

		try {

			File file = new File(dir + DocName);

			if (file.createNewFile()) {
				System.out.println("File is created!");
				_documents.put(id1, new Document(id1, DocName));
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = _documents.get(id1);
		sendAsJson(response, doc);
		return;

	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String[] splits = pathInfo.split("/");

		String docId = splits[1];
		String docNewName = splits[2];

		if (!_documents.containsKey(docId)) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Document doc = _documents.get(docId);

		String docOldName = doc.name;

		_documents.remove(docId);

		_documents.put(docId, new Document(docId, docNewName));

		doc = _documents.get(docId);

		try {

			File oldfile = new File(dir + docOldName);
			File newfile = new File(dir + docNewName);

			if (oldfile.renameTo(newfile)) {
				
			} else {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		sendAsJson(response, doc);

		return;
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		String pathInfo = request.getPathInfo();

		if (pathInfo == null || pathInfo.equals("/")) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String[] splits = pathInfo.split("/");

		if (splits.length != 2) {

			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String docId = splits[1];

		if (!_documents.containsKey(docId)) {

			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Document doc = _documents.get(docId);

		_documents.remove(docId);

		try {

			File file = new File(dir + doc.name);

			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		sendAsJson(response, doc);
		return;
	}

}
