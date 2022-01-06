package InsertCommentCommand;

import java.util.ArrayList;
import java.util.List;

//コメントの内容
public class InfoForFunctionComments {
    public String summary;
    public List<String> params = new ArrayList<>();
    public String returnContent;

    public int lineNum;
}
