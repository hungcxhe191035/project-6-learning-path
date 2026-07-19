# DATABASE TABLE DESCRIPTIONS

This document provides detailed descriptions of the database tables, including data fields, primary keys (PK), foreign keys (FK), unique constraints (UN), not-null constraints (NN), and the description of each field.

All tables in the system inherit from **BaseEntity**, thereby inheriting the audit columns: `delete_flag`, `created_at`, `created_by`, `updated_at`, and `updated_by`.

---

## 1. `users` Table (User Accounts)
* **Corresponding Entity:** [User.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/User.java)
* **Description:** Manages authentication details, profile basics, roles, account statuses, and bank details of users (students, instructors, admins).

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `user_id` | X |  | X | X | Unique identifier of the user |
| 2 | `email` |  |  | X | X | Email address used for logging in and receiving notifications |
| 3 | `password` |  |  |  | X | Secured and encrypted password of the account |
| 4 | `full_name` |  |  |  |  | Full name of the user |
| 5 | `phone` |  |  |  |  | Contact phone number |
| 6 | `role` |  |  |  | X | Account role in the system: `STUDENT`, `INSTRUCTOR`, `ADMIN` |
| 7 | `status` |  |  |  |  | Account status: `ACTIVE`, `PENDING_VERIFICATION`, `INACTIVE` |
| 8 | `bank_name` |  |  |  |  | Bank name for receiving payments or making transfers (e.g., Vietcombank, Techcombank) |
| 9 | `bank_code` |  |  |  |  | Bank code (e.g., VCB, TCB, MBB) |
| 10 | `bank_account_number` |  |  |  |  | Bank account number |
| 11 | `bank_account_holder` |  |  |  |  | Bank account holder name |
| 12 | `avatar_file_id` |  | X |  |  | Link to the user's avatar image |
| 13 | `reset_token` |  |  |  |  | Token code used for the password reset feature |
| 14 | `reset_token_expiry` |  |  |  |  | Expiration time of the password reset token |
| 15 | `delete_flag` |  |  |  | X | Logical deletion flag: `true` if deleted, `false` if active |
| 16 | `created_at` |  |  |  |  | Creation timestamp of the account |
| 17 | `created_by` |  |  |  |  | Name of the user or process that created the account |
| 18 | `updated_at` |  |  |  |  | Last update timestamp of the account information |
| 19 | `updated_by` |  |  |  |  | Name of the user who last updated the account |

---

## 2. `user_profile` Table (User Profiles)
* **Corresponding Entity:** [UserProfile.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/UserProfile.java)
* **Description:** Stores extra profile information and social media links of users, especially instructors.

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `profile_id` | X |  | X | X | Unique identifier of the user profile |
| 2 | `bio` |  |  |  |  | Self-introduction or brief autobiography |
| 3 | `headline` |  |  |  |  | Short headline shown below the user name |
| 4 | `facebook_url` |  |  |  |  | Link to the Facebook personal profile |
| 5 | `youtube_url` |  |  |  |  | Link to the personal YouTube channel |
| 6 | `linkedin_url` |  |  |  |  | Link to the LinkedIn personal profile |
| 7 | `user_id` |  | X | X |  | Link to the associated user account |
| 8 | `delete_flag` |  |  |  | X | Logical deletion flag |
| 9 | `created_at` |  |  |  |  | Creation timestamp of the profile |
| 10 | `created_by` |  |  |  |  | User who created the profile |
| 11 | `updated_at` |  |  |  |  | Last update timestamp of the profile |
| 12 | `updated_by` |  |  |  |  | User who last updated the profile |

---

## 3. `instructor_applications` Table (Instructor Applications)
* **Corresponding Entity:** [InstructorApplication.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/InstructorApplication.java)
* **Description:** Manages registration applications submitted by students wishing to become instructors, along with admin review notes.

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `application_id` | X |  | X | X | Unique identifier of the instructor application |
| 2 | `user_id` |  | X |  | X | ID of the student submitting the application |
| 3 | `headline` |  |  |  | X | Summary of the applicant's professional expertise |
| 4 | `bio` |  |  |  | X | Brief summary of work history, capabilities, and teaching skills |
| 5 | `motivation` |  |  |  | X | Motivation and goals for joining as an instructor |
| 6 | `linkedin_url` |  |  |  |  | Link to the applicant's LinkedIn profile for information verification |
| 7 | `cv_file_name` |  |  |  |  | File name of the attached curriculum vitae (CV) |
| 8 | `cv_file_path` |  |  |  |  | Storage path of the attached CV in the system |
| 9 | `status` |  |  |  | X | Application status: `PENDING`, `APPROVED`, `REJECTED` |
| 10 | `review_note` |  |  |  |  | Admin's review note explaining approval or rejection reason |
| 11 | `delete_flag` |  |  |  | X | Logical deletion flag |
| 12 | `created_at` |  |  |  |  | Submission timestamp of the application |
| 13 | `created_by` |  |  |  |  | User who submitted the application |
| 14 | `updated_at` |  |  |  |  | Last update or review timestamp of the application |
| 15 | `updated_by` |  |  |  |  | User who last updated or reviewed the application |

---

## 4. `files` Table (Uploaded Files Management)
* **Corresponding Entity:** [AppFile.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/AppFile.java)
* **Description:** Manages uploaded files in the system, such as avatars, certificates, homework attachments, and course materials.

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `file_id` | X |  | X | X | Unique identifier of the uploaded file |
| 2 | `file_name` |  |  |  | X | Original name of the file when uploaded |
| 3 | `file_url` |  |  |  | X | Full URL to access and view/download the file |
| 4 | `file_type` |  |  |  |  | Classification of the file format: `IMAGE`, `PDF`, `DOCX`, etc. |
| 5 | `extension` |  |  |  |  | File extension (e.g., `png`, `jpg`, `pdf`) |
| 6 | `purpose` |  |  |  |  | Intended purpose of the file: `CERTIFICATE`, `AVATAR`, `HOMEWORK`, etc. |
| 7 | `delete_flag` |  |  |  | X | Logical deletion flag |
| 8 | `created_at` |  |  |  |  | Timestamp when the file was uploaded |
| 9 | `created_by` |  |  |  |  | User who uploaded the file |
| 10 | `updated_at` |  |  |  |  | Last update timestamp of the file information |
| 11 | `updated_by` |  |  |  |  | User who last updated the file information |

---

## 5. `system_settings` Table (Admin System Settings)
* **Corresponding Entity:** [SystemSetting.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/SystemSetting.java)
* **Description:** Manages global configuration settings for the system, letting admins tune system parameters dynamically (e.g. commission rate, contact information) without code changes.

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `setting_id` | X |  | X | X | Unique identifier of the setting |
| 2 | `setting_key` |  |  | X | X | Unique setting key (e.g., `COMMISSION_RATE`) |
| 3 | `setting_value` |  |  |  | X | Corresponding setting value stored as a string |
| 4 | `description` |  |  |  |  | Detailed explanation of the function and value format of this setting |
| 5 | `delete_flag` |  |  |  | X | Logical deletion flag for the setting |
| 6 | `created_at` |  |  |  |  | Timestamp when the setting was first created |
| 7 | `created_by` |  |  |  |  | User who created the setting |
| 8 | `updated_at` |  |  |  |  | Last update timestamp of the setting value |
| 9 | `updated_by` |  |  |  |  | User who last updated the setting value |

---

## 6. `certificates` Table (Course Completion Certificates)
* **Corresponding Entity:** [Certificate.java](file:///c:/Users/Administrator/Downloads/project-6-learning-path/src/main/java/org/swp/my_learning_path/entity/Certificate.java)
* **Description:** Manages completion certificates awarded to students upon fulfilling course requirements.

| No | Field | PK | FK | UN | NN | Description |
| :--- | :--- | :---: | :---: | :---: | :---: | :--- |
| 1 | `certificate_id` | X |  | X | X | Unique identifier of the certificate |
| 2 | `enrollment_id` |  | X |  | X | ID of the associated course enrollment |
| 3 | `file_id` |  | X |  |  | ID of the published certificate image/PDF file |
| 4 | `certificate_code` |  |  | X | X | Unique certificate code used for verification |
| 5 | `delete_flag` |  |  |  | X | Logical deletion flag indicating invalid or deleted certificate |
| 6 | `created_at` |  |  |  |  | Timestamp when the certificate was issued |
| 7 | `created_by` |  |  |  |  | Issuing authority or system |
| 8 | `updated_at` |  |  |  |  | Last update timestamp of the certificate information |
| 9 | `updated_by` |  |  |  |  | User or system that last updated the certificate information |
