import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileBuilder {

    public static void generateHome(String filePath, List<Email> emails, int totalPages, int currentPage) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            writer.write("<html><head><title>Email Archive</title></head><body>");
            writer.write("<h1>Email Archive - Page " + currentPage + "</h1>");

            for (Email email : emails) {
                writer.write("<div>");
                writer.write("<h2><a href=\"mail" + email.hashCode() + ".html\">" + email.getSubject() + "</a></h2>");
                writer.write("<p>Date: " + email.getReceivedDate() + "</p>");
                writer.write("<p>From: " + email.getSender() + "</p>");
                writer.write("</div>");
            }

            writer.write("<div>");
            for (int i = 1; i <= totalPages; i++) {
                if (i == currentPage) {
                    writer.write("<span>" + i + "</span> ");
                } else {
                    writer.write("<a href=\"index_page_" + i + ".html\">" + i + "</a> ");
                }
            }
            writer.write("</div>");

            writer.write("</body></html>");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateFiles(String outputPath, Email email) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("<html>");
        sb.append("<head>");
        sb.append("<title>" + email.getSubject() + "</title>");
        sb.append("<style>");
        sb.append("a { text-decoration: none; }");
        sb.append(".email-content { display: block; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");

        sb.append("<div class='email-content'>");
        sb.append("<h1>" + email.getSubject() + "</h1>");
        sb.append("<p>From: " + email.getSender() + "</p>");
        sb.append("<p>Sent: " + email.getSentDate() + "</p>");
        if (!email.getAttachments().isEmpty()) {
            sb.append("<p>Attachments: " + email.getAttachments() + "</p>");
        }
        sb.append("<div>" + email.getContent() + "</div>");
        sb.append("</div>");

        sb.append("</body>");
        sb.append("</html>");

        try (FileWriter fw = new FileWriter(outputPath)) {
            fw.write(sb.toString());
        }
    }
}
