package ParserOfNeedCommand.Listener;

import File.FileInputer;
import File.InfoForNecessaryComments;
import ParserOfNeedCommand.Generated.CPP14BaseListener;
import ParserOfNeedCommand.Generated.CPP14Parser;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CommentsListener extends CPP14BaseListener {
    private CommonTokenStream tokens;
    private CPP14Parser parser;

    private ArrayList<String> results = new ArrayList<String>();

    public List<String> getResults() {
        return this.results;
    }

    private boolean isFunctiondefinition = false;
    private boolean isParametersandqualifiers = false;
    private boolean isEnumspecifier = false;
    private String classType = null;
    private String nestedClassType = null;

    private InfoForNecessaryComments infoObj;
    int previusComments = -100;

    public CommentsListener(CommonTokenStream tokens, CPP14Parser parser) {
        this.tokens = tokens;
        this.parser = parser;
        //TODO 設定ファイルをオプションで選択できるようにした方がいい？
        FileInputer fileInPuter = new FileInputer();
        fileInPuter.readSettingFileForNecessaryComments("Config/SettingForNecessaryComments.json");
        infoObj = fileInPuter.getInfoForNecessaryCommentsObj();
    }

    private class ctxInfo {
        ParserRuleContext ctx;
        int lineNum;
        boolean needsDecision;

        public ctxInfo(ParserRuleContext ctx, int lineNum, boolean b) {
            this.ctx = ctx;
            this.lineNum = lineNum;
            this.needsDecision = b;
        }

        public int getLineNum() {
            return lineNum;
        }
    }

    List<ctxInfo> ctxInfos = new ArrayList<>();

    //    //自作された型のポインタ
//    @Override
//    public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx){
//        determineWhetherCommentIsNecessary(ctx);
//    }

    //関数定義、メンバ関数定義の前
    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isFunction()));
        System.out.println("関数" + ctx.getStart().getLine());
        isFunctiondefinition = true;
    }

    @Override
    public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
        isFunctiondefinition = false;
    }

    //(メンバ?)関数宣言，変数宣言の前．
    @Override
    public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
        isParametersandqualifiers = false;
    }

    @Override
    public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
        if (isParametersandqualifiers) {
            //メンバ関数
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isClassMemberFunction()));
            System.out.println("メンバ関数" + ctx.getStart().getLine());
        } else if (Objects.equals(classType, "class")) {
                //クラスのメンバ変数
                ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isClassMemberVariables()));
                System.out.println("classメンバ変数" + ctx.getStart().getLine());
        } else if (Objects.equals(classType, "struct")) {
            //構造体のメンバ変数
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isStructMemberVariables()));
            System.out.println("structメンバ変数" + ctx.getStart().getLine());
        } else if (Objects.equals(classType, "union")) {
            //共用体のメンバ変数
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isUnionMemberVariables()));
            System.out.println("unionメンバ変数" + ctx.getStart().getLine());
        } else if (isEnumspecifier) {
            //列挙体
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isEnumStatement()));
            System.out.println("メンバenum" + ctx.getStart().getLine());
            isEnumspecifier = false;
        } else {
            //その他メンバ
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), false));
            System.out.println("その他メンバ" + ctx.getStart().getLine());
        }
    }

    //ParametersandqualifiersでMemberdeclarationが関数宣言？
    @Override
    public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
        isParametersandqualifiers = true;
    }

    //変数宣言、クラス,構造体の前
    @Override
    public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
        //TODO memberdeclartionで，simpletypespecifierになってるやつが変数名になる？
    }

    @Override
    public void enterClasskey(CPP14Parser.ClasskeyContext ctx) {
        if(classType == null) {
            classType = ctx.getText();
        }
    }

    @Override
    public void enterEnumspecifier(CPP14Parser.EnumspecifierContext ctx) {
        isEnumspecifier = true;
    }

    @Override
    public void exitSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
        if (Objects.nonNull(classType)) {
            if (Objects.equals(classType, "class")) {
                //クラス
                ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isClass()));
                System.out.println("クラス" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "struct")) {
                //構造体
                ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isStructure()));
                System.out.println("構造体" + ctx.getStart().getLine());
            } else if (Objects.equals(classType, "union")) {
                //共用体
                ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isUnion()));
                System.out.println("共用体" + ctx.getStart().getLine());
            } else {
                ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), false));
            }
            classType = null;
        } else if (isFunctiondefinition) {
            //関数内の変数
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isFunctionVariables()));
            System.out.println("関数内の変数" + ctx.getStart().getLine());
        }else if (isEnumspecifier) {
            //列挙体
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isEnumStatement()));
            System.out.println("enum" + ctx.getStart().getLine());
            isEnumspecifier = false;
        } else {
            //その他
            ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), false));
            System.out.println("グローバル変数" + ctx.getStart().getLine());
        }
    }

    //Loopの前
    @Override
    public void enterIterationstatement(CPP14Parser.IterationstatementContext ctx) {
        //Iterationstatementの最も左側のTokenを取得
        ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isIterationsStatement()));
    }

    //if,switchの前
    @Override
    public void enterSelectionstatement(CPP14Parser.SelectionstatementContext ctx) {
        ctxInfos.add(new ctxInfo(ctx, ctx.getStart().getLine(), infoObj.isSelectionsStatement()));
    }

    @Override public void enterTranslationunit(CPP14Parser.TranslationunitContext ctx) {
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        //ファイルへのコメントがある場合
        if(beforeCommentChannel != null) {
            if(beforeCommentChannel.get(0).getTokenIndex() == 0){
                previusComments = 0;
            }
        }
    }

    @Override
    public void exitTranslationunit(CPP14Parser.TranslationunitContext ctx) {
        ctxInfos.sort(Comparator.comparing(ctxInfo::getLineNum));
        for (ctxInfo c : ctxInfos) {
            if (c.needsDecision) {
                determineWhetherCommentIsNecessary(c.ctx);
            } else {
                determinePreviusComments(c.ctx);
            }
        }
        for (String result : results) {
            System.out.println(result);
        }
    }

    //ここがもっとも重要なコード
    private void determineWhetherCommentIsNecessary(ParserRuleContext ctx) {
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if (beforeCommentChannel != null) {
//            beforeIndex = beforeCommentChannel.size() -1 ;
            String str = beforeCommentChannel.toString();
            String sample = "This comment was written by SCCA.";
            if (str.contains(sample)) {
                outPutWhereSccaComments(startToken);
            }
        }

        //TODO 条件分岐が複雑すぎる．真理値表を参照．

        if (beforeCommentChannel == null && afterCommentChannel == null) {
            outPutWhereNeedToComments(startToken);
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                outPutWhereNeedToComments(startToken);
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex())) {
            } else {
                outPutWhereNeedToComments(startToken);
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
                    outPutWhereNeedToComments(startToken);
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }

    private void determinePreviusComments(ParserRuleContext ctx) {
        Token startToken = ctx.getStart();
        Token stopToken = ctx.getStop();
        List<Token> beforeCommentChannel = getBeforeHiddenTokens(ctx, 2);
        List<Token> afterCommentChannel = getAfterHiddenTokens(ctx, 2);

        int beforeIndex = 0;
        int afterIndex = 0;

        if (beforeCommentChannel != null) {
//            beforeIndex = beforeCommentChannel.size() -1 ;
            String str = beforeCommentChannel.toString();
            String sample = "This comment was written by SCCA.";
            if (str.contains(sample)) {
                outPutWhereSccaComments(startToken);
            }
        }

        if (beforeCommentChannel == null && afterCommentChannel == null) {
//            outPutWhereNeedToComments(startToken);
        } else if (beforeCommentChannel == null && afterCommentChannel != null) {
            //後にあるコメントがステートメントと同じ行にある．
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
//                outPutWhereNeedToComments(startToken);
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        } else if (beforeCommentChannel != null && afterCommentChannel == null) {
            //保持しているコメントと，以前のコメントが一緒じゃない．
            if ((previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex())) {
            } else {
//                outPutWhereNeedToComments(startToken);
            }
        } else {
            if (afterCommentChannel.get(afterIndex).getLine() != stopToken.getLine()) {
                if (previusComments != beforeCommentChannel.get(beforeIndex).getTokenIndex()) {
                } else {
//                    outPutWhereNeedToComments(startToken);
                }
            } else {
                previusComments = afterCommentChannel.get(afterIndex).getTokenIndex();
            }
        }
    }

    //隠れているトークンを取得
    private List<Token> getBeforeHiddenTokens(ParserRuleContext ctx, int type) {
        Token token = ctx.getStart();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToLeft(i, type);
        return CommentChannel;
    }

    //隠れているトークンを取得
    private List<Token> getAfterHiddenTokens(ParserRuleContext ctx, int type) {
        Token token = ctx.getStop();
        int i = token.getTokenIndex();
        //TODO 前にある複数のコメントを取りたい．今は一つだけしか取れない．なぜ
        List<Token> CommentChannel = this.tokens.getHiddenTokensToRight(i, type);
        return CommentChannel;
    }

    //コメントの出力の処理
    private void outPutWhereNeedToComments(Token token) {
        String msg = token.getLine() + "行目の " + token.getText() + "の前にコメントが必要です";

        //重複があるときは追加しないようにするための処理
        if (results.isEmpty()) {
            this.results.add(msg);
        } else if (msg.equals(results.get(results.size() - 1))) { //msgと，resultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
        }
    }

    //SCCAが作成したコメントの場合の処理
    private void outPutWhereSccaComments(Token token) {
        String msg = token.getLine() + "行目の " + token.getText() + "の前のコメントは修正が必要です";

        //重複があるときは追加しないようにするための処理
        if (results.isEmpty()) {
            this.results.add(msg);
        } else if (msg.equals(results.get(results.size() - 1))) { //msgと，resultsの最後の要素が一致してるなら，何もしない．

        } else {
            this.results.add(msg);
        }
    }
}