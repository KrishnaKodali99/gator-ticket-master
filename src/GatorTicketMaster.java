public class GatorTicketMaster {
    public static void main(String[] args) {
        FileIOProcessor fileIOProcessor = new FileIOProcessor();

        if (args.length < 1) {
            System.out.println("Error: Please provide a file name as an argument.");
            return;
        }

        String filePath = args[0];
        fileIOProcessor.processFile(filePath);
    }
}
