package com.unsupervisedsentiment.analysis.core.constants;

public class StanfordNLPTestConstants {

	public static final String SENTENCE_ONE = "The quick brown fox jumps over the lazy dog.";
	public static final String SENTENCE_TWO = "The quick brown fox jumps over the lazy dog. And the good dog mauled the fox.";

	public static final String SENTENCE_TEST1 = "The phone has a good screen.";
	public static final String SENTENCE_TEST2 = "IPOD is the best mp3 player.";
	public static final String SENTENCE_TEST3 = "Does the player play DVD with good audio and video?";
	public static final String SENTENCE_TEST4 = "Canon G3 has a great lens.";
	public static final String SENTENCE_TEST5 = "The camera is amazing and easy to use.";
	public static final String SENTENCE_TEST6 = "If you want to buy a sexy, cool, accessory-available mp3 player, you can choose IPod";
	public static final String SENTENCE_LIU = "Canon G3 takes great pictures. The picture is amazing. You may have to get more storage to store high quality pictures and recorded movies. The software is amazing.";

	public static final String HUGE_REVIEW = "A mystery is finally solved but is it? It depends on your point of view. As far as the Argeneau family is concerned, someone rescued a villain and must be brought to justice. But what if that person isn’t who they thought they were? What happens when the truth is even more bizarre than anyone could have imagined? Get ready to be amazed by reading Vampire Most Wanted. "
			+ "I suspected one truth but that was only the tip of the iceberg and why I found this novel so intriguing. It all revolves around a woman. She’s mysterious, she’s talented and she’s in danger – or is she? Her name is Divine and she is a very independent woman and is the heroine. She’s also nurturing, helpful, even loving, but it’s balanced with a healthy dose of fear, mistrust and confusion. The heroine has a lot of components to her personality that made me like her, feel sad for her, and cheer for her. There were other times where I was a bit irritated because I thought she was slow on the uptake. Ultimately, I found there was an actual reason for that failing – and it wasn’t necessarily her fault. "
			+ "I discovered so much about Divine until the light bulb went off in my head, ‘Aha’! When Ms. Sands decides to tie all the loose ends, she does a good job of it, Thing is, it took me a bit to realize that this dangling thread occurred in books past and I was wowed. At no time did I ever expect it to turn out as it did in Vampire Most Wanted. Also amazing is, after reading this book, the title can be interpreted two different ways. I found that very cool. "
			+ "Marcus, the hero, is in a fix. He has to be himself but pretend to be someone he’s not. Not the way to make a good first impression on one’s future life mate. I always enjoy how the author has her characters figure out who their life mate is because of their ‘symptoms’. Marcus has some serious strategizing to do in this book because Divine isn’t going to be easy to woo. Of course that means I had a fun time reading about all the fun they get into in the process. "
			+ "It wasn’t all amusement and games however. There is a very serious aspect of this story that alternated between disgusting me, horrifying me or down right making me sad enough to sniffle. Once the truth comes out, ALL the truths, it’s heartbreaking. What it comes down to is Divine having to make choices and some of them are going to be extremely tough to do. The author’s writing in this was powerful, dramatic and very emotional. I can’t imagine any reader remaining unaffected by the some of the revelations. "
			+ "When Marcus and Divine succumb to the mating frenzy, once again, Ms. Sands finds ways to keep it fresh, fascinating and at times, even funny. I expect readers to find themselves smiling here and there throughout the tale just because a few of the instances were hilarious, like the broom incident – oh my gosh did I get a giggle out of that. Not Marcus though, but Vincent did. Then again, Vincent recounted an incident with Jackie that totally had me snickering and giggling. So, yes, Ms. Sands continues with her tradition of humor even when the suspense is thick. However, when the passion is hot, the writing is smoking. "
			+ "Secondary characters are key in helping the plot move along at a nice clip. Mind reading is a clever trick. Dialogue is entertaining as usual and the location, the carnival, was unique and fun. "
			+ "Vampire Most Wanted has a lot of surprises in store for fans of the series. It’s a well-rounded, engrossing and engaging story with many charming interactions between the characters. It was interesting figuring Divine out and fascinating watching her and Marcus fall in love. This is a great addition to the long running series and a definite recommend. Even new fans can enjoy this book with the only caveat being that they might end up being curious to find and read all the books that came before. If this book accomplishes that, they’re going to be in for a treat because some are even better than this one, but all are worth reading. ";
	public static final String SMALL_TEST = "She was slow. She is not beautiful.";

	public static String generateNSentencesString(int n) {
		StringBuilder sentence = new StringBuilder("");
		for (int i = 0; i < n; i++) {
			sentence.append(SENTENCE_ONE);
			sentence.append(" ");
		}
		return sentence.toString();
	}
}
