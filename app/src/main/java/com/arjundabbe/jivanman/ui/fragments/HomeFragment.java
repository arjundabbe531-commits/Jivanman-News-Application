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

        // ======================= 12 August 2025 – आज =======================

        newsList.add(new NewsArticle(
                "भारताने 2030 पर्यंत कार्बन उत्सर्जनात 50% कपात करण्याचा उद्दिष्ट जाहीर केला",
                "पंतप्रधानांनी जगासमोर भारताच्या हरित उर्जेच्या दृष्टीने महत्वाकांक्षी योजना सादर केली, ज्यामुळे पर्यावरण संवर्धनात मोठा वाटा पडणार आहे.",
                "https://staticimg.amarujala.com/assets/images/2024/05/13/co2-pollution_89efcd3b4ccefc55e1aba9c6d4a2f8d3.jpeg?w=674&dpr=1.0&q=80",
                "12 Aug 2025 • 10:30 AM"
        ));

        newsList.add(new NewsArticle(
                "उद्धव ठाकरे यांचे ट्रम्पवर टीकास्त्र",
                "उद्धव ठाकरे म्हणाले—‘डोनाल्ड ट्रम्प पंतप्रधान मोदींची खिल्ली उडवत आहेत’; मोदींच्या उत्तरक्षमतेवर देखील निशाणा.",
                "https://etvbharatimages.akamaized.net/etvbharat/prod-images/07-08-2025/1200-675-24760441-thumbnail-16x9-uddhavthackeray-aspera.jpg??imwidth=1200",
                "12 Aug 2025 • 09:15 AM"
        ));
        newsList.add(new NewsArticle(
                "दुलीप ट्रॉफी—शुभमन गिलने संघनेत्तृत्व स्वीकारले",
                "भारताचे कसोटी कर्णधार शुभमन गिल उत्तरा विभागीय संघाचे नेतृत्व करणार; दुलीप ट्रॉफी क्वार्टर फायनल 28 ऑगस्टपासून.",
                "https://www.livehindustan.com/lh-img/smart/img/2025/08/07/1200x900/PTI07-30-2025-000351B-0_1754576366355_1754576389644.jpg",
                "12 Aug 2025 • 10:00 AM"
        ));
        newsList.add(new NewsArticle(
                "जेजुरीत पाच मजली इमारत खचली",
                "पुणे जिल्ह्यातील जेजुरी शहरेतील पाच मजली नवीन इमारत खचल्यामुळे नगरपालिका पोलिस बंदोबस्तात रात्रभर सुरक्षा; नंतर इमारत पाडण्यात आली.",
                "https://akm-img-a-in.tosshub.com/aajtak/images/story/202407/66893e8060da9-surat-building-collapse-065422990-16x9.jpg?size=948:533",
                "11 Aug 2025 • 09:57 PM"
        ));


        newsList.add(new NewsArticle(
                "नवी मुंबईत 'ग्रीन पार्क'चे उद्घाटन",
                "नवी मुंबई महापालिकेने शहरातील प्रदूषण कमी करण्यासाठी नवीन ग्रीन पार्कचा लोकार्पण केले; नागरिकांमध्ये पर्यावरण संवर्धनाबाबत जागरूकता वाढेल.",
                "https://translate.google.com/website?sl=en&tl=hi&hl=hi&client=imgs&u=https://images.timesproperty.com/blog/9881/1747639631_blogimage.webp",
                "12 Aug 2025 • 10:15 AM"
        ));
        newsList.add(new NewsArticle(
                "मुख्यमंत्री फडणवीस यांनी दिला 5000 मेगावॅट सौर उर्जेचा टप्पा पूर्ण करण्याचा 'सप्टेंबर 2025' पर्यंतचा आदेश",
                "केंद्रीय समीक्षा बैठकीत EV‑सौर धोरण ग्रामीण भागात जलद अंमलबजावणीसाठी सप्टेंबर 2025 पर्यंत 5000 मेगावॅट क्षमतेचा टप्पा पूर्ण करण्याचा आदेश.",
                "https://devgatha.in/wp-content/uploads/2025/04/devgatha-energy-minister-solar-man-1024x536.webp",
                "05 Aug 2025 • 06:45 AM"
        ));

        newsList.add(new NewsArticle(
                "शरद पवार – अजित पवार एकत्र? विधानसभा निवडणुकीसाठी नवा डावपेच!",
                "बारामतीमध्ये दोन्ही गटांमध्ये चर्चेचा धोरणात्मक प्रवास सुरु आहे; निवडणुकीपूर्वी राष्ट्रवादी काँग्रेसच्या एकत्र येण्याची शक्यता वर्धित.",
                "https://www.hindustantimes.com/ht-img/img/2025/04/21/550x309/Ajit-Pawar-Sharad-Pawar_1745254253589_1745254253893.jpg",
                "05 Aug 2025 • 10:30 AM"
        ));

        newsList.add(new NewsArticle(
                "गडचिरोलीत मुख्यमंत्री फडणवीस यांनी मेगा स्टील प्लांटची पायाभरणी केली",
                "4 MTPA क्षमतेच्या मेगा स्टील प्लांट व नवीन शाळा प्रकल्पाची घोषणा; स्थानिक बेरोजगारांसाठी रोजगार संधी निर्माण करण्याची योजना.",
                "https://marathi.indiatimes.com/thumb/123018317/thumb-123018317.jpg?imgsize-2658130&width=700&height=394&resizemode=75",
                "05 Aug 2025 • 10:15 AM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्र सरकार सुरू करणार AI-युक्त एकीकृत ई‑गव्हर्नन्स पोर्टल",
                "शासनाने बहुभाषिक AI‑चॅटबॉटसह व्हॉट्सॲप व वेबसाइट पद्धतींचे प्लॅटफॉर्म विकसित करण्याचा निर्णय घेतला आहे; विविध विभागांमध्ये डिजिटल सेवा एकत्रित करण्यासाठी.",
                "https://apacnewsnetwork.com/wp-content/uploads/2025/04/News-97.webp",
                "05 Aug 2025 • 09:00 AM"
        ));

        newsList.add(new NewsArticle(
                "पुण्यातील M.B. Camp शाळेत स्मार्ट क्लासरूमचे लोकार्पण",
                "एनएक्सपी इंडियाच्या सहकार्याने शाळेत डिजिटल शिक्षणाचा अनुभव तयार केला; विद्यार्थ्यांसाठी एज्युकेशन फेअर देखील आयोजित.",
                "https://marathi.indiatimes.com/thumb/123085179/pune-news-123085179.jpg?imgsize=136712&width=700&height=394&resizemode=75",
                "05 Aug 2025 • 08:30 AM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्र, दिल्ली, चंदीगड इलेक्ट्रिक मोबिलिटीमध्ये आघाडीवर",
                "NITI Aayog च्या India Electric Mobility Index नुसार या राज्यांमध्ये EV तंत्रज्ञान, चार्जिंग नेटवर्क आणि प्रेरित आर्थिक धोरणे आघाडीवर आहेत.",
                "https://img.etimg.com/thumb/msid-123095769,width-300,height-225,imgsize-3896,resizemode-75/niti-aayog-working-on-state-wise-science-tech-outlook-dashboard.jpg",
                "05 Aug 2025 • 08:00 AM"
        ));

        newsList.add(new NewsArticle(
                "बॉम्बे HC चा 5वा बेंच कोल्हापूरमध्ये सुरू होणार",
                "18 ऑगस्टपासून कोल्हापूर, सातारा, सांगली, सोलापूर, नांदेड व हिंगोली विभागांसाठी बॉम्बे HC चा ५वा बेंच सुरू केल्याने न्याय अधिक उपलब्ध होईल.",
                "https://images.indianexpress.com/2025/08/gavai-sambhajiraje.jpg?w=640",
                "05 Aug 2025 • 07:15 AM"
        ));

        newsList.add(new NewsArticle(
                "Mumbai–Pune corridor: NHAI निर्णय दिला द्रुतगती महामार्ग बांधण्याचा",
                "NHAI ने मुंबई-पुणे महामार्गाला समांतर दुसरा expressway बांधण्याचा निर्णय घेतला आहे; नवीन नॅश्नल एव्हेन्यूस पोर्टलवर प्रस्ताव जारी.",
                "https://krushimarathi.com/wp-content/uploads/2025/03/Krushi-Marathi-97-1.jpg",
                "04 Aug 2025 • 09:00 PM"
        ));

        newsList.add(new NewsArticle(
                "मनसे अध्यक्ष राज ठाकरे पुण्यातील मेळाव्यात भाषण देणार",
                "‘मराठा गौरव मेळाव’ पुण्यात मुंबई‑ठाणे महामार्गावर आयोजित; राज ठाकरेंनी समाजातील ऐक्य वाढवण्यासाठी आवाहन केले.",
                "https://images.tv9marathi.com/wp-content/uploads/2023/03/23160403/MNS-Raj-Thackeray-1.jpg?w=1280",
                "04 Aug 2025 • 06:45 PM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्र मंत्रिमंडळात मोठा फेरबदल होण्याची शक्यता",
                "शिंदे सरकार नवनिर्वाचित चेहऱ्यांना संधी देण्याच्या विचारांत असून, पक्षातील राजकीय हालचालींमध्ये गती आली आहे.",
                "https://images.tv9marathi.com/wp-content/uploads/2024/06/CM-Eknath-Shinde-Ajit-Pawar-Devendra-Fadnavis.jpg?w=1280",
                "04 Aug 2025 • 03:20 PM"
        ));

        newsList.add(new NewsArticle(
                "महाराष्ट्रात 'Innovation City' स्थापन करण्याचा निर्णय – मुख्यमंत्री घोषणे",
                "AI‑आधारित स्टार्टअप व उद्योजकता वाढवण्यासाठी, SIDBI सह ₹100 कोटी निधीचा लाभ घेऊन एक आधुनिक Innovation City तयार करण्याचा निर्णय.",
                "https://images.navarashtra.com/wp-content/uploads/2025/01/start-up-day_V_png--1280x720-4g.webp?sw=1366&dsz=1280x720&iw=850&p=false&r=1",
                "04 Aug 2025 • 11:15 AM"
        ));

        newsList.add(new NewsArticle(
                "India जगात Generative AI वापरात दूसरे स्थानावर",
                "Lucknow मध्ये IET परिषदेत भारताची Generative AI वापरातील जागतिक स्थिती व भविष्यातील धोरणात्मक वाटचाल चर्चा विषय.",
                "https://feeds.abplive.com/onecms/images/uploaded-images/2025/01/30/4702373cd022a3c1070388cecd598c8b17382202510811164_original.png?impolicy=abp_cdn&imwidth=1200&height=675",
                "03 Aug 2025 • 09:20 AM"
        ));

        newsList.add(new NewsArticle(
                "IIIT हैदराबादमध्ये नवीन FabLab: देशाच्या semiconductor संशोधनात मोठी भर",
                "IIIT‑Hyderabad मध्ये FabLab सुरू; मायक्रोफॅब्रिकेशन, चिप डिझाइन व सिंथेसिस मध्ये संशोधनासाठी सुवर्ण संधी.",
                "https://blogs.iiit.ac.in/wp-content/uploads/2025/07/FabLab-3.jpg",
                "02 Aug 2025 • 04:30 PM"
        ));

        newsList.add(new NewsArticle(
                "DRDO ने 2000+ तंत्रज्ञान हस्तांतरण करार केले, 200 उत्पादन परवाने दिले",
                "DRDO ने भारतात संरक्षण उद्योगातील आत्मनिर्भरतेसाठी 2000 हून अधिक तंत्रज्ञान हस्तांतरण करार केले आणि 200 उत्पादन परवाने दिले.",
                "https://indiadarpanlive.com/wp-content/uploads/2025/06/PICV8YK-750x375.jpeg",
                "01 Aug 2025 • 06:00 PM"
        ));

        newsList.add(new NewsArticle(
                "माणिकराव कोकाटे यांचे कृषी खाते काढून क्रीडा मंत्री पदाअभिषिक्त",
                "रमी खेळण्याचा व्हिडीओ व्हायरल झाल्यानंतर कृषी खाते काढण्यात आले; आता Youth & Sports विभागाची जबाबदारी माणिकराव कोकाटे यांच्याकडे.",
                "https://www.shabdakhadag.in/uploads/product/01082025125050_Kokate...jpg",
                "01 Aug 2025 • 08:39 PM"
        ));

        return newsList;
    }


}
