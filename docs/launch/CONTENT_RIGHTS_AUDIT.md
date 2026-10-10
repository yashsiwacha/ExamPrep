# ExamPrepOS — Content Rights & Intellectual Property Audit

**Document Version:** 1.0.0  
**Date:** October 2026  
**Auditor / Role:** IP & Content Compliance Specialist  
**Verification Scope:** Pre-bundled Question Bank (2,500 questions), Syllabi Metadata, Brand Assets, and Open Source Licenses  

---

## 1. Content Origin & Question Bank Rights

ExamPrepOS bundles 25 national pattern papers (2,500 questions total, 500 questions per exam vertical across JEE Main, NEET UG, UPSC Prelims, CAT, and GATE CS) in `data/src/main/assets/content/`.

### Verified Content Lineage:
1. **Original Educational Formulation:**  
   Questions and numerical problem sets are modeled on standard national curriculum benchmarks (NCERT Class 11 & 12 Physics, Chemistry, Biology, and standard undergraduate engineering curricula) and publicly released question formats.
2. **Zero Third-Party Commercial Infringement:**  
   No content has been extracted, copied, or reproduced from proprietary commercial coaching institutions (e.g., Allen Career Institute, FIITJEE, Aakash Institute, Resonance, or PW private test papers).
3. **Step-by-Step Explanations:**  
   All mathematical derivations, formula explanations, and conceptual rationales are authored independently, providing original pedagogical value.

---

## 2. Trademark & Examination Nomenclature Fair Use

The application references national examination titles:
- *JEE Main (Joint Entrance Examination)*
- *NEET UG (National Eligibility cum Entrance Test)*
- *UPSC CSE Prelims (Civil Services Examination)*
- *CAT (Common Admission Test)*
- *GATE (Graduate Aptitude Test in Engineering)*

### Fair Use Legal Basis (Nominative Fair Use):
Under Indian Trademark Law (Trade Marks Act, 1999, Section 30) and international nominative fair use doctrine, the use of an examination name is permissible when:
1. The product or service cannot be identified without using the trademarked name.
2. Only so much of the mark is used as is reasonably necessary to identify the exam.
3. The user does not suggest sponsorship or endorsement by the trademark holder.

### Mandatory Protection Implemented:
Every user-facing document, app description, and in-app legal dialog carries the prominent **Non-Affiliation Disclaimer**:
> *"ExamPrepOS is an independent educational productivity tool and is NOT affiliated with, endorsed by, authorized by, or sponsored by the National Testing Agency (NTA), Union Public Service Commission (UPSC), Indian Institutes of Technology (IITs), Indian Institutes of Management (IIMs), or any government examination authority."*

---

## 3. Open Source Software (OSS) License Attribution

Inspected file: `docs/legal/OPEN_SOURCE_LICENSES.md` and in-app `Settings -> Open Source Licenses` screen.

All third-party libraries integrated into ExamPrepOS are distributed under permissive open-source licenses:
- **Jetpack Compose, AndroidX, WorkManager, Hilt, Room:** Apache License 2.0 (Google LLC).
- **Kotlin Standard Library, Coroutines, Serialization:** Apache License 2.0 (JetBrains s.r.o.).
- **Material 3 Icons:** Apache License 2.0.

### Compliance Verification:
- ✅ Full text of the Apache License 2.0 and MIT License included in `OPEN_SOURCE_LICENSES.md`.
- ✅ Complete in-app interactive viewer accessible to users in `SettingsScreen`.
- ✅ Zero GPL / copyleft viral licenses that would require disclosing proprietary application source code.
