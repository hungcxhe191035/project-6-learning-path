# USER MANUAL
## MY LEARNING PATH SYSTEM

---

## 1. OVERVIEW

The **My Learning Path** platform is an online learning portal that connects **Students** and **Instructors** under the supervision and coordination of the **Administrator (Admin)**. The platform integrates a personal digital wallet system, making transactions such as buying courses, depositing funds, and withdrawing earnings fast, secure, and convenient.

The system segregates access and functions using three primary roles:
*   **Student**: Registers an account, deposits funds into their personal wallet via the bank gateway, searches for and purchases courses, and views lectures. Students with professional expertise can submit an application to become an Instructor.
*   **Instructor**: Drafts course curriculums, registers courses, sets pricing, issues custom course discount vouchers, tracks shared revenue, and withdraws funds directly to their personal bank account.
*   **Administrator (Admin)**: Manages and monitors the entire system. Admins have the authority to approve instructor applications, review course content and temporarily lock courses, manage user accounts (role assignment, locking/unlocking accounts), monitor transaction logs, configure system revenue-sharing rates, and manage category tags.

---

## 2. USER MANUAL

### 2.1. Dashboard (Admin Overview Panel)

#### 2.1.1. Purpose
Provides the Administrator (Admin) with a comprehensive overview of sales performance, new user registrations, accumulated revenue, and overall system activity via key performance indicators and statistical charts updated in real-time.

#### 2.1.2. Operational Steps
1.  **Filter Data by Time Period**:
    *   On the main Dashboard screen, click the time filter selection in the top-right corner.
    *   Select the reporting timeframe:
        *   **Today**: Statistics generated during the current calendar day.
        *   **7 Days**: Review system performance over the past 7 days.
        *   **30 Days**: Review performance over the past 30 days (Default).
        *   **This Year**: Year-to-date summary starting from January 1st of the current year.
        *   **All Time**: Cumulative statistics since the system launched.
2.  **Monitor Key Metrics**:
    *   **Total Revenue**: Total funds collected from all successfully paid course orders on the system.
    *   **Total Students / Instructors**: The number of active accounts registered under each role.
    *   **Total Courses**: Total number of courses created on the system.
    *   **Pending Instructor Applications**: Number of student instructor requests awaiting review.
    *   **Pending Courses**: Number of new lectures or updated course versions awaiting Admin review.
3.  **Read Statistical Charts**:
    *   **Revenue Trend Chart**: Illustrates daily revenue fluctuations over the selected timeframe. Even on days with zero sales, the system renders a data point of 0 to ensure chart continuity, allowing for seamless trend analysis.
    *   **Student Tag Distribution Chart**: Shows the registration share based on course topics. Hovering over each slice displays the exact enrollment count and percentage to help identify top-performing categories.
4.  **Rankings & Recent Activity**:
    *   Provides quick-access tables for top-selling courses, outstanding instructors, and recent orders or pending approvals requiring immediate action.

---

### 2.2. User Management (Admin Panel)

#### 2.2.1. Purpose
Enables Admins to track profile details, perform searches, reassign roles, and lock/unlock user accounts either individually or in bulk.

#### 2.2.2. Operational Steps
1.  **Search and Filter Users**:
    *   Go to the user management page in the Admin navigation bar.
    *   Use the search box: Search by Full Name or Email (case-insensitive search).
    *   Use the Role filter to isolate accounts: Student or Instructor.
    *   Use the Status filter to select active or inactive (locked) accounts.
    *   Click the search button. Use pagination at the bottom of the list to navigate the results.
2.  **View User Profile Details**:
    *   Click the details button next to a user.
    *   The profile detail view displays personal information, social links, linked banking details, current wallet balance, and a full history of deposits, withdrawals, and course payments.
3.  **Lock or Unlock Accounts**:
    *   **Individual Action**: In the list actions column or within the user details page, click lock to suspend a user violating terms, or click unlock to restore their access.
    *   **Bulk Actions**:
        *   Tick the checkboxes next to the users you want to process. Tick the header checkbox to select all users on the current page.
        *   Once selected, the bulk actions bar will appear at the bottom.
        *   Click bulk lock or bulk unlock.
        *   Confirm the operation in the popup modal.
    *   *Self-lock Protection*: To prevent Admins from accidentally locking themselves out, the selection checkbox and lock buttons for the currently logged-in Admin are disabled.
4.  **Assign Roles**:
    *   Click the change role button next to a user.
    *   Select the target role (Student or Instructor) and save. Promoting a user to an Admin role is strictly blocked to maintain system role security.
5.  **Create Users Manually**:
    *   Click the add new user button.
    *   Fill in the required fields: Email (must be unique), Password, Full Name, Phone, Role, and initial Account Status.
    *   Click save. The system secures the password and automatically creates an associated personal wallet with a starting balance of 0.

---

### 2.3. Approve Instructor (Admin Panel)

#### 2.3.1. Purpose
Allows Admins to review credentials, biography, teaching motivations, and CV PDFs submitted by Students applying to become Instructors, and approve or reject the requests.

#### 2.3.2. Operational Steps
1.  **View Application Queue**:
    *   Go to the instructor approvals page.
    *   The list displays all applications in pending status, sorted from newest to oldest.
2.  **Evaluate Applications**:
    *   Click the review button next to a pending application.
    *   The details page shows: Professional tagline, biography, motivation, social media links, and proposed teaching topics.
    *   Click the attached CV link (PDF format) to read or download the document for detailed review.
3.  **Approve Application**:
    *   If the profile meets standards, add optional notes in the review field.
    *   Click the approve button and confirm.
    *   **Outcome**: The application status updates to approved. The system elevates the user's role from Student to Instructor. The system displays a congratulatory notification on the user's dashboard and sends a notification email to their mailbox.
4.  **Reject Application**:
    *   If the application is insufficient, the Admin must input a rejection reason in the review field (e.g., "The attached CV is blurry, please upload a high-resolution scan").
    *   Click reject and confirm.
    *   **Outcome**: The application status updates to rejected. The user's role remains Student. The system sends a dashboard notification and an email highlighting the rejection reason.

---

### 2.4. Tag Management (Admin Panel)

#### 2.4.1. Purpose
Enables Admins to manage the tags that classify courses and instructors' focus areas (e.g., Java, Python, Graphic Design).

#### 2.4.2. Operational Steps
The view features a split layout:
1.  **Create a New Tag**:
    *   On the form on the left side, input the tag name and a brief description. Tag names must be unique.
    *   Click save. The tag will appear in the table on the right.
    *   *Automatic Tag Reactivation*: If the Admin creates a tag that matches the name of a previously deleted tag, the system restores the old tag and updates its description, bypassing database errors and keeping the database clean.
2.  **Edit an Existing Tag**:
    *   In the table on the right side, click edit next to the target tag.
    *   The tag details load into the left form.
    *   Update the name or description, then click update.
3.  **Delete a Tag**:
    *   In the table, click the delete icon next to the tag.
    *   Confirm the deletion in the popup prompt.
    *   **Business Safeguard**: To prevent system errors when a tag is active on courses or pending instructor applications, the system automatically removes associations in related records before marking the tag as deleted from the active list.

---

### 2.5. Course Management (Admin Panel)

#### 2.5.1. Purpose
Allows Admins to evaluate curriculum quality, block violating courses, and unblock or delete courses.

#### 2.5.2. Operational Steps
1.  **Search and Filter Courses**:
    *   Go to the course management page.
    *   Search by course title or Instructor name.
    *   Filter courses by status (Pending approval, Published, Draft) or block status (Blocked, Active).
    *   Click search.
2.  **Review Course Curriculum**:
    *   Click a course to access its details.
    *   The curriculum displays in a clear hierarchical list. Click a section header to expand and review lesson titles, video durations, and references.
3.  **Block a Violating Course**:
    *   If a course violates system terms or contains unauthorized material, click block.
    *   A modal requests the Admin to enter the block reason (mandatory).
    *   Click confirm block.
    *   **Outcome**: The course is flagged as blocked. The course is hidden from the search catalog and homepage, preventing new purchases.
    *   *Active Students Protection*: Students who purchased the course prior to it being blocked retain full access to continue their studies.
4.  **Unblock a Course**:
    *   Once the instructor updates the content, the Admin can click unblock on the course details page to restore its active status.
5.  **Delete a Course**:
    *   Click delete to remove a course from the active database records.

---

### 2.6. Wallet and Transaction Management (Admin Panel)

#### 2.6.1. Purpose
Allows Admins to monitor cash flow, perform transaction audits, and dynamically adjust the system's commission and revenue-sharing rates.

#### 2.6.2. Operational Steps
1.  **Review Transaction Logs**:
    *   Go to the wallet and transactions page.
    *   The table displays transaction ID, user account, amount, transaction type (Deposit, Withdrawal, Payment), transaction status, and timestamp.
    *   **Color-Coded Cash Flow**:
        *   Deposits are marked with a green plus sign (e.g., `+ 200,000 VND`).
        *   Outflows from the user's wallet (Withdrawal, Payment) are marked with a red minus sign (e.g., `- 450,000 VND`).
2.  **Configure Revenue Share Percent**:
    *   In the revenue share input field, enter a percentage between 0 and 100 (e.g., `80` means the Instructor gets 80% of sales, and the system collects a 20% operating fee).
    *   Click save setting.
    *   Once saved, all subsequent course purchases calculate splits based on this updated percentage.

---

### 2.7. Deposit/Withdraw Money (For Students & Instructors)

#### 2.7.1. Depositing Funds into Personal Wallets
1.  Log in, click your avatar at the top right, and select **My Wallet**.
2.  Click the deposit button.
3.  Enter the deposit amount (must be greater than 0) and choose the **VNPay** payment gateway. Click proceed.
4.  The system redirects you to the secure VNPay payment interface.
5.  Complete the payment on the VNPay portal (by scanning a QR code or entering your bank account details).
6.  Once payment is finished, you are redirected back to the learning portal. The deposit amount is automatically credited to your personal wallet and the transaction status is marked as successful.

#### 2.7.2. Withdrawing Wallet Balance to Bank Accounts
1.  **Configure Recipient Bank Account**:
    *   Go to your personal settings.
    *   Fill in bank details: Bank name, account number, and full account holder name (uppercase without accents) and save.
2.  **Submit Withdrawal Request**:
    *   Go to **My Wallet** -> Select the **Withdrawal** tab.
    *   Enter the amount to withdraw (must be greater than 0 and cannot exceed your current wallet balance).
    *   Click submit withdrawal.
    *   The system immediately verifies your balance, deducts the amount, and marks the transaction as successful, releasing it to the linked bank account details without manual Admin approval.

---

### 2.8. Submit Instructor Application (For Students)

#### 2.8.1. Purpose
Allows active students to submit their professional profile and upload their PDF CV for Admin review to apply for an upgrade to the Instructor role.

#### 2.8.2. Pre-requisites & Rules
*   The applicant must not already hold the Instructor or Admin role.
*   Only one application in pending status is permitted at a time. The system blocks form access if an application is already in progress.

#### 2.8.3. Steps to Execute
1.  Log in as a Student, and select the become an instructor option in the navigation bar.
2.  Fill in the registration form: Professional headline, biography, motivation, LinkedIn profile link, and proposed teaching topics.
3.  **Upload CV**:
    *   Upload your CV file in PDF format (other formats are blocked for security) with a maximum file size of 10MB.
    *   *Security Note*: The system accepts only PDF files. The uploaded file is automatically renamed and appended with a secure identifier to prevent overriding or leaking other users' data.
4.  Click submit application. The application will enter the pending status for Admin review.
5.  **Smart Re-application**:
    *   If your previous application was rejected, returning to the page automatically pre-fills the text fields from your previous submission to save time.
    *   Update any incorrect information and upload a new CV if required.
    *   When uploading a new CV file, the system automatically deletes the obsolete file of the rejected application from the server disk to keep storage clean and efficient.
