package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import java.net.URLEncoder

object IntentHelper {

    fun openWhatsApp(context: Context, phoneNumber: String, message: String = "") {
        try {
            val cleanPhone = cleanPhoneNumber(phoneNumber)
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = if (cleanPhone.isNotEmpty()) {
                "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMessage"
            } else {
                "https://api.whatsapp.com/send?text=$encodedMessage"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "WhatsApp খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun openBkash(context: Context) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.bKash.customerapp")
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            // Dial USSD or open PlayStore
            dialUssd(context, "*247#")
        }
    }

    fun openNagad(context: Context) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.konasl.nagad")
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            dialUssd(context, "*167#")
        }
    }

    fun openRocket(context: Context) {
        val launchIntent = context.packageManager.getLaunchIntentForPackage("com.dbbl.mbs.apps.main")
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            dialUssd(context, "*322#")
        }
    }

    fun openTelegram(context: Context, usernameOrPhone: String = "", message: String = "") {
        try {
            val clean = usernameOrPhone.trim().removePrefix("@")
            val url = if (clean.isNotEmpty()) {
                "https://t.me/$clean"
            } else {
                "https://t.me"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Telegram খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun openMessenger(context: Context, messengerId: String = "") {
        try {
            val clean = messengerId.trim()
            val url = if (clean.isNotEmpty()) {
                "https://m.me/$clean"
            } else {
                "https://m.me"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Messenger খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun openGoogleMaps(context: Context, address: String) {
        try {
            val encoded = URLEncoder.encode(address, "UTF-8")
            val uri = Uri.parse("geo:0,0?q=$encoded")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Google Maps খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun addToCalendar(context: Context, title: String, description: String, beginTimeMs: Long) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.Events.DESCRIPTION, description)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTimeMs)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, beginTimeMs + 60 * 60 * 1000)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "ক্যালেন্ডার খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendSms(context: Context, phoneNumber: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:${cleanPhoneNumber(phoneNumber)}")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "SMS অ্যাপ খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun makePhoneCall(context: Context, phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${phoneNumber.trim()}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "ডায়াল করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareText(context: Context, text: String, title: String = "হিসাব শেয়ার করুন") {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, title).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(shareIntent)
        } catch (_: Exception) {
            Toast.makeText(context, "শেয়ার করা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dialUssd(context: Context, ussdCode: String) {
        try {
            val encodedUssd = Uri.encode(ussdCode)
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$encodedUssd")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "ডায়ালার খোলা সম্ভব হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cleanPhoneNumber(phone: String): String {
        var p = phone.replace(" ", "").replace("-", "").trim()
        if (p.startsWith("01") && p.length == 11) {
            p = "88$p"
        } else if (p.startsWith("+88")) {
            p = p.removePrefix("+")
        }
        return p
    }
}
