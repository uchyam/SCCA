package File;

import InsertCommentCommand.GenerateComment;
import InsertCommentCommand.InfoForComment;
import InsertCommentCommand.InfoForFunctionComments;
import ParserOfNeedCommand.Listener.CommentsListener;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileRewriter{
    private String fileName;
    private File filePath;
    //書き込むファイルのファイルパス
    private File newFilePath;

    public FileRewriter(File filePath){
        this.filePath = filePath;
        this.fileName = filePath.getName();

        this.newFilePath = new File("Results/" +fileName);
    }

    public void outPutRewriteFile(List<InfoForComment> ifcs)  {
        try{
            if (checkBeforeWriteFile(newFilePath)){
                processToRewriteToFile(ifcs, newFilePath);
            }else if(newFilePath.createNewFile()){
                processToRewriteToFile(ifcs, newFilePath);
            }else{
                System.out.println("ファイルに書き込めません");
            }
        }catch(IOException e){
            System.out.println(e);
        }

    }

    //ファイルに書き込む処理
    private void processToRewriteToFile(List<InfoForComment> ifcs, File file) throws IOException {
        //BufferedReaderを作成．
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //ファイルから読み込む
        LinkedList<String> list = getList(bufferedReader);
        //コメントの挿入
        List<String> newList = addComment(ifcs, list);

        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8")));

        //日付を追加する．
//        Calendar c = Calendar.getInstance();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        pw.println(sdf.format(c.getTime()));

        for(String result:newList){
            pw.println(result);
        }
        pw.close();
    }

    //コメント
    private List<String> addComment(List<InfoForComment> ifcs, LinkedList<String> list){
        int index = -1;
        int count = 0;
        List<String> comment;

        for (InfoForComment ifc: ifcs){
            if (ifc.lineNum == 0) continue;
            StringBuilder indent = new StringBuilder();
            String str = list.get(ifc.lineNum+index);
            String[] strArray = str.split("");
            for(String s : strArray) {
                if(Objects.equals(s, " ") || Objects.equals(s, "\t")){
                    indent.append(s);
                }else {
                    break;
                }
            }
            GenerateComment generateComment = new GenerateComment(indent.toString(), ifc);
            comment = generateComment.getComment();
            list.addAll(ifc.lineNum + index, comment);
            index += comment.size();
            count++;
        }

        return list;
    }

    private List<String> createComment(String indent, InfoForComment ifc){
        List<String> comment = new ArrayList<>();
        comment.add(indent+"/**");
        comment.add(indent+" *@brief");
        if (ifc.params != null) {
            for (String s: ifc.params) {
                comment.add(indent + " *@param " + s);
            }
        }
        if (ifc.returnContent != null) {
            comment.add(indent + " *@return " + ifc.returnContent);
        }
        comment.add(indent+" *@attention This comment was written by SCCA.");
        comment.add(indent+" */");
        return comment;
    }

    //ファイルから読み込む
    private LinkedList<String> getList(BufferedReader bufferedReader) {
        LinkedList<String> list = new LinkedList<>();
        String string = null;
        try {
            string = bufferedReader.readLine();
            while (string != null){
                list.add(string);
                string = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private boolean checkBeforeWriteFile(File file){
        if (file.exists()) {
            if (file.isFile() && file.canWrite()) {
                return true;
            }
        }
        return false;
    }
}
