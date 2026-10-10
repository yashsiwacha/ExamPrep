# ExamPrepOS — Content Rights & Intellectual Property Audit

> **Audit Type**: Intellectual Property, Copyright, and Trademark Verification  
> **Applicable Laws & Regulations**: Indian Copyright Act, 1957, Trade Marks Act, 1999, Google Play Intellectual Property & Impersonation Policy  
> **Status**: Comprehensive Repository Audit Completed  
> **Date**: October 2026

---

## 1. Executive Summary

ExamPrepOS provides study syllabus planning, spaced-repetition flashcards, and mock examination papers for candidates preparing for competitive exams in India:
- **JEE Main** (Engineering)
- **NEET UG** (Medical)
- **UPSC Civil Services Examination** (Government Services)
- **CAT** (Management Admissions)
- **GATE CS** (Engineering Post-Graduate / PSU Recruitment)

This audit evaluates the copyright ownership, licensing, trademark usage, and commercial distribution rights for all educational assets bundled with or downloaded by the application.

---

## 2. Content Source & Copyright Matrix

| Content Category | Specific Assets | Source / Origin | Ownership Status | Commercial Rights | Legal Risk / Action Taken |
|---|---|---|---|---|---|
| **Question Bank (Bundled Seed Data)** | 50+ seed questions in `DatabaseSeedData.kt` | Created for ExamPrepOS (`source: ORIGINAL`) | Proprietary / Original Work | **Full Rights** | Low risk. Synthetic, independently authored questions with custom numerical values and detailed explanations. |
| **Downloadable Question Papers** | 25 JSON papers (5 per exam, 100 Qs each) in `assets/content/` | Authored synthetically (`source: ORIGINAL`, `year: 2026`) | Proprietary / Original Synthetic Mocks | **Full Rights** | Low risk. Questions are mathematically and conceptually formulated to match official syllabus patterns without verbatim copying of past government papers. |
| **Syllabus Structures & Taxonomies** | Subject, Chapter, and Topic outlines for JEE, NEET, UPSC, CAT, GATE | Derived from publicly notified examination notifications and NCERT curricula | Factual syllabus outlines (Non-copyrightable under Indian Copyright Act Sec 13; *Feist* doctrine of originality applies) | **Permitted under Fair Use** | Informational indexing only. No proprietary study materials, textbooks, or paid coaching materials are bundled. |
| **Step-by-Step Explanations** | Pedagogical solutions to all MCQs | Independently written by educational content developers | Proprietary / Original Work | **Full Rights** | Low risk. Explanations do not quote third-party coaching solutions. |
| **Exam Names & Acronyms** | "JEE Main", "NEET UG", "UPSC CSE", "CAT", "GATE" | Statutory/organizational examination titles | Registered/common law trademarks of respective conducting authorities (NTA, UPSC, IIMs, IITs) | **Nominative Fair Use** | Non-affiliation disclaimer prominently added to in-app Settings, Dialogs, Sign-Up screen, and Store Listing copy. |
| **App Icons & Vector Graphics** | Material 3 icons (`androidx.compose.material.icons`) | Android Open Source Project / Google Material Symbols | Apache 2.0 License | **Full Commercial Rights** with Apache 2.0 attribution | Pass. Included in open-source attribution register. |
| **Government / Official Logos** | Official seals of NTA, UPSC, Government of India, IITs, IIMs | **NONE FOUND** | **NOT BUNDLED** | **PROHIBITED** | **PASS**: Verified that the repository contains NO government crests, Ashoka Lion emblems, official logos, or deceptive branding. |
| **PDF Documents** | Pre-compiled PDF question booklets | **NONE FOUND** | All papers rendered dynamically from structured JSON via native Compose UI | **N/A** | Pass. No external scanned PDF dumps are distributed. |

---

## 3. Examination Authority Trademark Analysis & Fair Use

Under Section 30 of the Indian Trade Marks Act, 1999 and the doctrine of **Nominative Fair Use**:
- The use of an established mark by a third party to refer to the goods or services of the trademark holder is permissible provided:
  1. It is not used in a manner that misleads the public into believing there is a commercial connection, partnership, or sponsorship.
  2. The use is strictly necessary to identify the intended purpose or subject matter of the application.
  3. No official emblems or protected national crests are used (State Emblem of India (Prohibition of Improper Use) Act, 2005).

### Technical Safeguards Implemented:
1. **Unambiguous Disclaimer Modal**: Added in Settings under *Settings → Privacy & Legal → Official Disclaimer & Non-Affiliation*.
2. **Prominent Textual Clarification**: Added to Sign-Up screen stating:
   > *"ExamPrepOS is an independent preparation platform not affiliated with any exam conducting agency."*
3. **App Store Metadata Policy**: Prohibits using phrasing like *"Official JEE App"* or *"Government UPSC Test Series"*. All store listings must use *"Independent Self-Study & Revision Tool"*.

---

## 4. Publisher Action Items & Recommendations

1. **Verify Originality of Additional Mock Sets**: If third-party coaching institutes or freelance educators supply future question banks, ensure a formal **Content Assignment and Copyright Warranty Agreement** is executed confirming all questions and solutions are original.
2. **Never Claim Direct Official PYQ Affiliation**: If actual Past Year Questions (PYQs) are added in future updates, verify whether the conducting body (e.g., NTA, UPSC) releases them into the public domain for non-commercial/commercial educational use or requires explicit citation. For now, synthetic original mocks remain the safest compliance approach.
