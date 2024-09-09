/*
 * Copyright (c) 2024-2024, kaiyu.shan@outlook.com.
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
package baby.mumu.extension.ocr.tess4j;

import lombok.Getter;

/**
 * tess4j语言枚举
 *
 * @author <a href="mailto:kaiyu.shan@outlook.com">kaiyu.shan</a>
 * @since 2.0.0
 */
@SuppressWarnings("SpellCheckingInspection")
@Getter
public enum Tess4jLanguageEnum {

  AFR("afr"),
  AMH("amh"),
  ARA("ara"),
  ASM("asm"),
  AZE("aze"),
  AZE_CYRL("aze_cyrl"),
  BEL("bel"),
  BEN("ben"),
  BOD("bod"),
  BOS("bos"),
  BRE("bre"),
  BUL("bul"),
  CAT("cat"),
  CEB("ceb"),
  CES("ces"),
  CHI_SIM("chi_sim"),
  CHI_SIM_VERT("chi_sim_vert"),
  CHI_TRA("chi_tra"),
  CHI_TRA_VERT("chi_tra_vert"),
  CHR("chr"),
  COS("cos"),
  CYM("cym"),
  DAN("dan"),
  DEU("deu"),
  DEU_LATF("deu_latf"),
  DIV("div"),
  DZO("dzo"),
  ELL("ell"),
  ENG("eng"),
  EPO("epo"),
  EQU("equ"),
  EST("est"),
  EUS("eus"),
  FAO("fao"),
  FAS("fas"),
  FIL("fil"),
  FIN("fin"),
  FRA("fra"),
  FRM("frm"),
  FRY("fry"),
  GLA("gla"),
  GLE("gle"),
  GLG("glg"),
  GRC("grc"),
  GUJ("guj"),
  HAT("hat"),
  HEB("heb"),
  HIN("hin"),
  HRV("hrv"),
  HUN("hun"),
  HYE("hye"),
  IKU("iku"),
  IND("ind"),
  ISL("isl"),
  ITA("ita"),
  ITA_OLD("ita_old"),
  JAV("jav"),
  JPN("jpn"),
  JPN_VERT("jpn_vert"),
  KAN("kan"),
  KAT("kat"),
  KAT_OLD("kat_old"),
  KAZ("kaz"),
  KHM("khm"),
  KIR("kir"),
  KMR("kmr"),
  KOR("kor"),
  LAO("lao"),
  LAT("lat"),
  LAV("lav"),
  LIT("lit"),
  LTZ("ltz"),
  MAL("mal"),
  MAR("mar"),
  MKD("mkd"),
  MLT("mlt"),
  MON("mon"),
  MRI("mri"),
  MSA("msa"),
  MYA("mya"),
  NEP("nep"),
  NLD("nld"),
  NOR("nor"),
  OCI("oci"),
  ORI("ori"),
  OSD("osd"),
  PAN("pan"),
  POL("pol"),
  POR("por"),
  PUS("pus"),
  QUE("que"),
  RON("ron"),
  RUS("rus"),
  SAN("san"),
  SCRIPT_ARABIC("script/Arabic"),
  SCRIPT_ARMENIAN("script/Armenian"),
  SCRIPT_BENGALI("script/Bengali"),
  SCRIPT_CANADIAN_ABORIGINAL("script/Canadian_Aboriginal"),
  SCRIPT_CHEROKEE("script/Cherokee"),
  SCRIPT_CYRILLIC("script/Cyrillic"),
  SCRIPT_DEVANAGARI("script/Devanagari"),
  SCRIPT_ETHIOPIC("script/Ethiopic"),
  SCRIPT_FRAKTUR("script/Fraktur"),
  SCRIPT_GEORGIAN("script/Georgian"),
  SCRIPT_GREEK("script/Greek"),
  SCRIPT_GUJARATI("script/Gujarati"),
  SCRIPT_GURMUKHI("script/Gurmukhi"),
  SCRIPT_HANS("script/HanS"),
  SCRIPT_HANS_VERT("script/HanS_vert"),
  SCRIPT_HANT("script/HanT"),
  SCRIPT_HANT_VERT("script/HanT_vert"),
  SCRIPT_HANGUL("script/Hangul"),
  SCRIPT_HANGUL_VERT("script/Hangul_vert"),
  SCRIPT_HEBREW("script/Hebrew"),
  SCRIPT_JAPANESE("script/Japanese"),
  SCRIPT_JAPANESE_VERT("script/Japanese_vert"),
  SCRIPT_KANNADA("script/Kannada"),
  SCRIPT_KHMER("script/Khmer"),
  SCRIPT_LAO("script/Lao"),
  SCRIPT_LATIN("script/Latin"),
  SCRIPT_MALAYALAM("script/Malayalam"),
  SCRIPT_MYANMAR("script/Myanmar"),
  SCRIPT_ORIYA("script/Oriya"),
  SCRIPT_SINHALA("script/Sinhala"),
  SCRIPT_SYRIAC("script/Syriac"),
  SCRIPT_TAMIL("script/Tamil"),
  SCRIPT_TELUGU("script/Telugu"),
  SCRIPT_THAANA("script/Thaana"),
  SCRIPT_THAI("script/Thai"),
  SCRIPT_TIBETAN("script/Tibetan"),
  SCRIPT_VIETNAMESE("script/Vietnamese"),
  SIN("sin"),
  SLK("slk"),
  SLV("slv"),
  SND("snd"),
  SPA("spa"),
  SPA_OLD("spa_old"),
  SQI("sqi"),
  SRP("srp"),
  SRP_LATN("srp_latn"),
  SUN("sun"),
  SWA("swa"),
  SWE("swe"),
  SYR("syr"),
  TAM("tam"),
  TAT("tat"),
  TEL("tel"),
  TGK("tgk"),
  THA("tha"),
  TIR("tir"),
  TON("ton"),
  TUR("tur"),
  UIG("uig"),
  UKR("ukr"),
  URD("urd"),
  UZB("uzb"),
  UZB_CYRL("uzb_cyrl"),
  VIE("vie"),
  YID("yid"),
  YOR("yor");

  private final String value;

  Tess4jLanguageEnum(String value) {
    this.value = value;
  }

}