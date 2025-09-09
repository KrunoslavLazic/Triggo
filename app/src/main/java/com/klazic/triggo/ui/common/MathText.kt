package com.klazic.triggo.ui.common

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MathText(latex: String, modifier: Modifier = Modifier, displayMode: Boolean = false) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT } },
        update = { web ->
            val wrapped = if (displayMode) "$$ $latex $$" else "\\( $latex \\)"
            fun escapeHtml(s: String) = s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
            val body = escapeHtml(wrapped)
            val html = """
                <!doctype html>
                <html>
                  <head>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <link rel="stylesheet"
                          href="https://cdn.jsdelivr.net/npm/katex@0.16.10/dist/katex.min.css"/>
                    <style>
                      html, body { margin:0; padding:0; }
                      body { font-size:16px; }
                      #c { padding: 0; }
                    </style>
                    <script defer
                            src="https://cdn.jsdelivr.net/npm/katex@0.16.10/dist/katex.min.js"></script>
                    <script defer
                            src="https://cdn.jsdelivr.net/npm/katex@0.16.10/dist/contrib/auto-render.min.js"
                            onload="renderMathInElement(document.body,{
                              delimiters:[
                                {left:'$$', right:'$$', display:true},
                                {left:'\\(', right:'\\)', display:false}
                              ],
                              throwOnError:false
                            });"></script>
                  </head>
                  <body>
                    <div id="c">$body</div>
                  </body>
                </html>
            """.trimIndent()
            web.loadDataWithBaseURL(
                 null,
                 html,
                 "text/html",
                 "utf-8",
                 null)
        }
    )
}