/*
 * Copyright (c) 2024-2025, the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package baby.mumu.basis.enums;

import org.jetbrains.annotations.NotNull;

/**
 * 语言偏好枚举
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">Kaiyu Shan</a>
 * @see <a href="https://zh.wikipedia.org/wiki/ISO_639-1">ISO 639-1</a>
 * @since 1.0.1
 */
@SuppressWarnings({"LombokGetterMayBeUsed"})
public enum LanguageEnum {
  AA("aa", "Afar"),
  AB("ab", "Abkhazian"),
  AE("ae", "Avestan"),
  AF("af", "Afrikaans"),
  AK("ak", "Akan"),
  AM("am", "Amharic"),
  AN("an", "Aragonese"),
  AR("ar", "Arabic"),
  AS("as", "Assamese"),
  AV("av", "Avaric"),
  AY("ay", "Aymara"),
  AZ("az", "Azerbaijani"),
  BA("ba", "Bashkir"),
  BE("be", "Belarusian"),
  BG("bg", "Bulgarian"),
  BH("bh", "Bihari languages"),
  BI("bi", "Bislama"),
  BM("bm", "Bambara"),
  BN("bn", "Bengali"),
  BO("bo", "Tibetan"),
  BR("br", "Breton"),
  BS("bs", "Bosnian"),
  CA("ca", "Catalan"),
  CE("ce", "Chechen"),
  CH("ch", "Chamorro"),
  CO("co", "Corsican"),
  CR("cr", "Cree"),
  CS("cs", "Czech"),
  CU("cu", "Church Slavic"),
  CV("cv", "Chuvash"),
  CY("cy", "Welsh"),
  DA("da", "Danish"),
  DE("de", "German"),
  DV("dv", "Divehi"),
  DZ("dz", "Dzongkha"),
  EE("ee", "Ewe"),
  EL("el", "Greek"),
  EN("en", "English"),
  EO("eo", "Esperanto"),
  ES("es", "Spanish"),
  ET("et", "Estonian"),
  EU("eu", "Basque"),
  FA("fa", "Persian"),
  FF("ff", "Fulah"),
  FI("fi", "Finnish"),
  FJ("fj", "Fijian"),
  FO("fo", "Faroese"),
  FR("fr", "French"),
  FY("fy", "Western Frisian"),
  GA("ga", "Irish"),
  GD("gd", "Scottish Gaelic"),
  GL("gl", "Galician"),
  GN("gn", "Guarani"),
  GU("gu", "Gujarati"),
  GV("gv", "Manx"),
  HA("ha", "Hausa"),
  HE("he", "Hebrew"),
  HI("hi", "Hindi"),
  HO("ho", "Hiri Motu"),
  HR("hr", "Croatian"),
  HT("ht", "Haitian Creole"),
  HU("hu", "Hungarian"),
  HY("hy", "Armenian"),
  HZ("hz", "Herero"),
  IA("ia", "Interlingua"),
  ID("id", "Indonesian"),
  IE("ie", "Interlingue"),
  IG("ig", "Igbo"),
  II("ii", "Sichuan Yi"),
  IK("ik", "Inupiaq"),
  IO("io", "Ido"),
  IS("is", "Icelandic"),
  IT("it", "Italian"),
  IU("iu", "Inuktitut"),
  JA("ja", "Japanese"),
  JV("jv", "Javanese"),
  KA("ka", "Georgian"),
  KG("kg", "Kongo"),
  KI("ki", "Kikuyu"),
  KJ("kj", "Kwanyama"),
  KK("kk", "Kazakh"),
  KL("kl", "Kalaallisut"),
  KM("km", "Central Khmer"),
  KN("kn", "Kannada"),
  KO("ko", "Korean"),
  KR("kr", "Kanuri"),
  KS("ks", "Kashmiri"),
  KU("ku", "Kurdish"),
  KV("kv", "Komi"),
  KW("kw", "Cornish"),
  KY("ky", "Kirghiz"),
  LA("la", "Latin"),
  LB("lb", "Luxembourgish"),
  LG("lg", "Ganda"),
  LI("li", "Limburgan"),
  LN("ln", "Lingala"),
  LO("lo", "Lao"),
  LT("lt", "Lithuanian"),
  LU("lu", "Luba-Katanga"),
  LV("lv", "Latvian"),
  MG("mg", "Malagasy"),
  MH("mh", "Marshallese"),
  MI("mi", "Maori"),
  MK("mk", "Macedonian"),
  ML("ml", "Malayalam"),
  MN("mn", "Mongolian"),
  MR("mr", "Marathi"),
  MS("ms", "Malay"),
  MT("mt", "Maltese"),
  MY("my", "Burmese"),
  NA("na", "Nauru"),
  NB("nb", "Norwegian Bokmål"),
  ND("nd", "North Ndebele"),
  NE("ne", "Nepali"),
  NG("ng", "Ndonga"),
  NL("nl", "Dutch"),
  NN("nn", "Norwegian Nynorsk"),
  NO("no", "Norwegian"),
  NR("nr", "South Ndebele"),
  NV("nv", "Navajo"),
  NY("ny", "Nyanja"),
  OC("oc", "Occitan"),
  OJ("oj", "Ojibwa"),
  OM("om", "Oromo"),
  OR("or", "Oriya"),
  OS("os", "Ossetian"),
  PA("pa", "Punjabi"),
  PI("pi", "Pali"),
  PL("pl", "Polish"),
  PS("ps", "Pashto"),
  PT("pt", "Portuguese"),
  QU("qu", "Quechua"),
  RM("rm", "Romansh"),
  RN("rn", "Rundi"),
  RO("ro", "Romanian"),
  RU("ru", "Russian"),
  RW("rw", "Kinyarwanda"),
  SA("sa", "Sanskrit"),
  SC("sc", "Sardinian"),
  SD("sd", "Sindhi"),
  SE("se", "Northern Sami"),
  SG("sg", "Sango"),
  SI("si", "Sinhala"),
  SK("sk", "Slovak"),
  SL("sl", "Slovenian"),
  SM("sm", "Samoan"),
  SN("sn", "Shona"),
  SO("so", "Somali"),
  SQ("sq", "Albanian"),
  SR("sr", "Serbian"),
  SS("ss", "Swati"),
  ST("st", "Southern Sotho"),
  SU("su", "Sundanese"),
  SV("sv", "Swedish"),
  SW("sw", "Swahili"),
  TA("ta", "Tamil"),
  TE("te", "Telugu"),
  TG("tg", "Tajik"),
  TH("th", "Thai"),
  TI("ti", "Tigrinya"),
  TK("tk", "Turkmen"),
  TL("tl", "Tagalog"),
  TN("tn", "Tswana"),
  TO("to", "Tonga"),
  TR("tr", "Turkish"),
  TS("ts", "Tsonga"),
  TT("tt", "Tatar"),
  TW("tw", "Twi"),
  TY("ty", "Tahitian"),
  UG("ug", "Uighur"),
  UK("uk", "Ukrainian"),
  UR("ur", "Urdu"),
  UZ("uz", "Uzbek"),
  VE("ve", "Venda"),
  VI("vi", "Vietnamese"),
  VO("vo", "Volapük"),
  WA("wa", "Walloon"),
  WO("wo", "Wolof"),
  XH("xh", "Xhosa"),
  YI("yi", "Yiddish"),
  YO("yo", "Yoruba"),
  ZA("za", "Zhuang"),
  ZH("zh", "Chinese"),
  ZH_TW("zh-TW", "Chinese (Traditional)"),
  ZU("zu", "Zulu");

  private final String code;
  private final String name;

  LanguageEnum(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public static @NotNull LanguageEnum fromCode(String code) {
    for (LanguageEnum lang : LanguageEnum.values()) {
      if (lang.code.equalsIgnoreCase(code)) {
        return lang;
      }
    }
    throw new IllegalArgumentException("Unknown language code: " + code);
  }
}

