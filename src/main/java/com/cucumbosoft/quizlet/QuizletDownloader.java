package com.cucumbosoft.quizlet;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class QuizletDownloader {
    private final HttpClient connection = HttpClientBuilder.create().build();
    private final List<String> words    = new ArrayList<>();
    private Document page;

    public void downloader() {
        String pageUrl   = JOptionPane.showInputDialog("enter quizlet url", JOptionPane.PLAIN_MESSAGE);
        Object separator = JOptionPane.showInputDialog(null,
                "select separator", "input",
                JOptionPane.INFORMATION_MESSAGE, null,
                new String[] {": ", " - "}, ": "
        );

        try {
            page = Jsoup.parse(EntityUtils.toString(connection.execute(new HttpGet(pageUrl)).getEntity()));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        words.addAll(
                page.select(".terms > *").stream()
                    .map(card ->
                        card.attr("class", "text")
                            .attr("class", "word has-audio ")
                            .getElementsByTag("h3")
                            .first()
                            .text() +
                            separator +
                        card.attr("class", "text")
                            .attr("class", "definition has-audio ")
                            .getElementsByTag("span")
                            .last()
                            .text() +
                        "\n"
                    )
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList())
        );

        // set clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.join("", words)), null);

        // alert user to completion
        JOptionPane.showMessageDialog(null, "Copied " + words.size() + " words.", "Complete", JOptionPane.PLAIN_MESSAGE);
    }
}
