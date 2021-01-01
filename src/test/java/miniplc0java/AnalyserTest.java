package miniplc0java;

import miniplc0java.analyser.Analyser;
import miniplc0java.error.CompileError;
import miniplc0java.tokenizer.StringIter;
import miniplc0java.tokenizer.Tokenizer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;

import static org.junit.Assert.*;

public class AnalyserTest {
    public static void main(String[] args) throws CompileError {
        var inputFileName = "input.txt";
        var outputFileName = "output_ll.txt";
        //设置输入
        InputStream input;
        try {
            input = new FileInputStream(inputFileName);
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find input file.");
            e.printStackTrace();
            System.exit(2);
            return;
        }
        //设置输出
        PrintStream output;
        try {
            output = new PrintStream(new FileOutputStream(outputFileName));
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open output file.");
            e.printStackTrace();
            System.exit(2);
            return;
        }
        Scanner scanner;
        scanner = new Scanner(input);
        var iter = new StringIter(scanner);
        var tokenizer = tokenize(iter);
        // analyze
        Analyser analyzer = new Analyser(tokenizer);
        try {
            analyzer.analyse();
        } catch (Exception e) {
            // 遇到错误输出，直接退出
            System.err.println(e);
            System.exit(0);
            return;
        }
        //输出每个Instruction的tostring
        output.println(analyzer.toAnalyserString());

    }

    private static Tokenizer tokenize(StringIter iter) {
        var tokenizer = new Tokenizer(iter);
        return tokenizer;
    }
}
