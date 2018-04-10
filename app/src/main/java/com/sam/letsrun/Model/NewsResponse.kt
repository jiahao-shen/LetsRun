package com.sam.letsrun.Model

/**
 * 新闻响应
 */
data class NewsResponse(val reason: String,
                        val result: NewsResult,
                        val error_code: Int)
