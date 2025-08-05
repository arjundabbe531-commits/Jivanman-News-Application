package com.arjundabbe.jivanman.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arjundabbe.jivanman.R;
import com.arjundabbe.jivanman.adapters.NewsAdapter;
import com.arjundabbe.jivanman.models.NewsArticle;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;

    @SuppressLint("MissingInflatedId")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.homeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        adapter  = new NewsAdapter(getActivity(), getSampleNews());
        recyclerView.setAdapter(adapter);


        return view;
    }
    private List<NewsArticle> getSampleNews() {
        List<NewsArticle> newsList = new ArrayList<>();

        newsList.add(new NewsArticle(
                "शरद पवार – अजित पवार एकत्र? विधानसभा निवडणुकीसाठी नवा डावपेच!",
                "बारामतीत दोन्ही गटांमध्ये भेटीचे सत्र सुरु; निवडणुकीपूर्वी राष्ट्रवादी एकत्र येण्याची शक्यता.",
                "https://www.hindustantimes.com/ht-img/img/2025/04/21/550x309/Ajit-Pawar-Sharad-Pawar_1745254253589_1745254253893.jpg",
                "05 Aug 2025 • 10:30 AM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्र, दिल्ली, चंदीगड इलेक्ट्रिक मोबिलिटीमध्ये आघाडीवर",
                "NITI Aayog च्या India Electric Mobility Index नुसार या राज्यांमध्ये EV ढांच्याची उन्नती.",
                "https://img.etimg.com/thumb/msid-123095769,width-300,height-225,imgsize-3896,resizemode-75/niti-aayog-working-on-state-wise-science-tech-outlook-dashboard.jpg",
                "05 Aug 2025 • 08:00 AM"
        ));

        newsList.add(new NewsArticle(
                "Mumbai–Pune corridor: NHAI निर्णय दिला द्रुतगती महामार्ग बांधण्याचा",
                "नवी मुंबई विमानतळ पुढील प्रवासातील वाढ लक्षात घेऊन नैऋत्य महामार्गाला समांतर दुसरा Expressway प्रस्तावित.",
                "https://krushimarathi.com/wp-content/uploads/2025/03/Krushi-Marathi-97-1.jpg",
                "04 Aug 2025 • 09:00 PM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्र मंत्रिमंडळात मोठा फेरबदल होण्याची शक्यता",
                "शिंदे सरकार नव्या चेहऱ्यांना संधी देण्याच्या विचारात; पक्षांतर्गत हालचाली वाढल्या.",
                "https://images.tv9marathi.com/wp-content/uploads/2024/06/CM-Eknath-Shinde-Ajit-Pawar-Devendra-Fadnavis.jpg?w=1280",
                "04 Aug 2025 • 03:20 PM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्रात 'Innovation City' स्थापन करण्याचा निर्णय – मुख्यमंत्री घोषणे",
                "AI‑आधारित स्टार्टअप व उद्योजकत्वाला गती देण्यासाठी SIDBI सह ₹100 कोटी निधीची घोषणा.",
                "https://images.navarashtra.com/wp-content/uploads/2025/01/start-up-day_V_png--1280x720-4g.webp?sw=1366&dsz=1280x720&iw=850&p=false&r=1",
                "04 Aug 2025 • 11:15 AM"
        ));

        newsList.add(new NewsArticle(
                "India जगात Generative AI वापरात दूसरे स्थानावर",
                "Lucknow मध्ये IET परिषदेत भारताची जागतिक स्थिती व भविष्याचा आराखडा चर्चिला.",
                "https://feeds.abplive.com/onecms/images/uploaded-images/2025/01/30/4702373cd022a3c1070388cecd598c8b17382202510811164_original.png?impolicy=abp_cdn&imwidth=1200&height=675",
                "03 Aug 2025 • 09:20 AM"
        ));

        newsList.add(new NewsArticle(
                "IIIT हैदराबादमध्ये नवीन FabLab: देशाच्या semiconductor संशोधनात मोठी भर",
                "दक्षता वाढीसाठी माईक्रोफॅब्रिकेशन आणि चिप अभ्यास केंद्र सुरु.",
                "https://blogs.iiit.ac.in/wp-content/uploads/2025/07/FabLab-3.jpg",
                "02 Aug 2025 • 04:30 PM"
        ));

        newsList.add(new NewsArticle(
                "DRDO ने 2000+ तंत्रज्ञान हस्तांतरण करार केले, 200 उत्पादन परवाने दिले",
                "देशात संरक्षण उद्योगात आत्मनिर्भरतेचा टप्पा साध्य करण्यासाठी DRDO चा महत्त्वाचा पुढाकार.",
                "https://indiadarpanlive.com/wp-content/uploads/2025/06/PICV8YK-750x375.jpeg",
                "01 Aug 2025 • 06:00 PM"
        ));

        newsList.add(new NewsArticle(
                "माणिकराव कोकाटे यांचे कृषी खाते काढून क्रीडा मंत्री पदाअभिषिक्त",
                "रमी खेळण्याचा व्हिडीओ व्हायरल झाल्यानंतर कृषी खाते काढले; आता Youth & Sports Dept ची जबाबदारी सोपवली.",
                "https://www.shabdakhadag.in/uploads/product/01082025125050_Kokate...jpg",
                "01 Aug 2025 • 08:39 PM"
        ));


        newsList.add(new NewsArticle(
                "मनसे अध्यक्ष राज ठाकरे पुण्यातील मेळाव्यात भाषण देणार",
                "पुण्यात मुंबई-ठाणे महामार्गावर ‘मराठा गौरव मेळावा’ आयोजित केला; राज ठाकरेंनी समाजातून एकत्रता वाढवण्याचे आवाहन केले.",
                "https://images.tv9marathi.com/wp-content/uploads/2023/03/23160403/MNS-Raj-Thackeray-1.jpg?w=1280",
                "04 Aug 2025 • 06:45 PM"
        ));

// ⚙️ Government E‑governance platform launch
        newsList.add(new NewsArticle(
                "महाराष्ट्र सरकार सुरू करणार AI-युक्त एकीकृत ई‑गव्हर्नन्स पोर्टल",
                "विद्यालये, उद्योग, मजुरी आणि खाणी विभागांसाठी ₹4.95 कोटींच्या निधीने बहुभाषिक AI चॅटबॉटसह व्हॉट्सॲप आणि वेबसाइट प्लेटफॉर्म विकसित केला जात आहे.",
                "https://apacnewsnetwork.com/wp-content/uploads/2025/04/News-97.webp",
                "05 Aug 2025 • 09:00 AM"
        ));

// 🏭 Gadchiroli Mega Steel Plant inauguration
        newsList.add(new NewsArticle(
                "गडचिरोलीत मुख्यमंत्री फडणवीस यांनी मेगा स्टील प्लांटची पायाभरणी केली",
                "आत्मनिर्भरतेसाठी 4 MTPA स्टील प्लांट व सीबीएसई शाळा प्रकल्पाची घोषणा; स्थानिक बेरोजगारांना रोजगाराची संधी.",
                "https://marathi.indiatimes.com/thumb/123018317/thumb-123018317.jpg?imgsize-2658130&width=700&height=394&resizemode=75",
                "05 Aug 2025 • 10:15 AM"
        ));

        newsList.add(new NewsArticle(
                "पुण्यातील M.B. Camp शाळेत स्मार्ट क्लासरूमचे लोकार्पण",
                "एनएक्सपी इंडियाच्या सहयोगाने शिक्षण डिजिटल झाली; विद्यार्थी, पालक आणि शिक्षकांनी सहभागी होऊन एज्युकेशन फेअरही आयोजित.",
                "https://marathi.indiatimes.com/thumb/123085179/pune-news-123085179.jpg?imgsize-136712&width=700&height=394&resizemode=75",
                "05 Aug 2025 • 08:30 AM"
        ));


        newsList.add(new NewsArticle(
                "बॉम्बे HC चा 5वा बेंच कोल्हापूरमध्ये सुरू होणार",
                "18 ऑगस्टपासून कोल्हापूर, सातारा, सांगली, सोलापूर, नांदेड व हिंगोलीतील नागरिकांना न्याय सुलभ व्हावा हा हेतू.",
                "https://images.indianexpress.com/2025/08/gavai-sambhajiraje.jpg?w=640",
                "05 Aug 2025 • 07:15 AM"
        ));


        newsList.add(new NewsArticle(
                "मुख्यमंत्री फडणवीस यांनी दिला 5000 मेगावॅट सौर उर्जेचा टप्पा पूर्ण करण्याचा 'सप्टेंबर 2025' पर्यंतचा आदेश",
                "मुंबईतील काल केंद्रीय समीक्षा बैठक; EV‑सौर धोरण ग्रामीण महाराष्ट्रात गती लावणार.",
                "https://devgatha.in/wp-content/uploads/2025/04/devgatha-energy-minister-solar-man-1024x536.webp",
                "05 Aug 2025 • 06:45 AM"
        ));



        return newsList;
    }


}
