<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Learning Path – Learn English Through Community Roadmaps</title>
        <meta name="description" content="A community-driven English learning platform – Create your own roadmap, share for free, and learn together."/>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700;800;900&display=swap" rel="stylesheet"/>
        <link rel="stylesheet" href="assets/css/style.css"/>
    </head>
    <body>

        <jsp:include page="header.jsp" />

        <!-- ===== HERO ===== -->
        <section class="hero" id="hero">
            <div class="hero-bg-dots"></div>
            <div class="container">
                <div class="hero-content">
                    <div class="hero-badge">✨ 100% Free · Open Community</div>
                    <h1>
                        Learn English<br/>
                        <span class="gradient-text">Your Own Way</span>
                    </h1>
                    <p class="hero-desc">
                        Create your own learning roadmap, share it with the community,
                        and study together with thousands of learners.
                        Completely free — forever.
                    </p>
                    <div class="hero-btns">
                        <a href="#" class="btn-hero-primary" id="hero-start">Create My Roadmap →</a>
                        <a href="#explore" class="btn-hero-ghost" id="hero-explore">Explore Existing Roadmaps</a>
                    </div>
                    <div class="hero-stats">
                        <div class="hstat"><strong id="cnt-users">12,400+</strong><span>Learners</span></div>
                        <div class="hstat-div"></div>
                        <div class="hstat"><strong id="cnt-paths">3,800+</strong><span>Roadmaps</span></div>
                        <div class="hstat-div"></div>
                        <div class="hstat"><strong id="cnt-free">100%</strong><span>Free</span></div>
                    </div>
                </div>

                <div class="hero-visual">
                    <div class="visual-card main-card">
                        <div class="vc-header">
                            <div class="vc-avatar" style="background:#7c3aed">T</div>
                            <div>
                                <div class="vc-name">Trong Duc</div>
                                <div class="vc-sub">created a roadmap</div>
                            </div>
                            <div class="vc-badge">IELTS 7.0</div>
                        </div>
                        <div class="vc-title">🗺️ IELTS Roadmap from Beginner → 7.0 in 8 Months</div>
                        <div class="vc-steps">
                            <div class="vs-item done"><div class="vs-dot done"></div><span>Basic Grammar Foundation</span><span class="vs-tag">4 weeks</span></div>
                            <div class="vs-item done"><div class="vs-dot done"></div><span>Topic Vocabulary Practice</span><span class="vs-tag">6 weeks</span></div>
                            <div class="vs-item active"><div class="vs-dot active"></div><span>IELTS 4 Skills Training</span><span class="vs-tag">12 weeks</span></div>
                            <div class="vs-item"><div class="vs-dot"></div><span>Real Exam Practice</span><span class="vs-tag">8 weeks</span></div>
                        </div>
                        <div class="vc-footer">
                            <span>👥 2,341 learning</span>
                            <button class="btn-join">Join Now</button>
                        </div>
                    </div>

                    <div class="visual-card float-card fc-1">
                        <div style="font-size:20px">🎯</div>
                        <div>
                            <div class="fc-title">New Member!</div>
                            <div class="fc-sub">Le Phuong Linh</div>
                        </div>
                    </div>

                    <div class="visual-card float-card fc-2">
                        <div style="font-size:20px">🏆</div>
                        <div>
                            <div class="fc-title">Completed Step 2</div>
                            <div class="fc-sub">+120 XP earned</div>
                        </div>
                    </div>

                    <div class="visual-card float-card fc-3">
                        <div style="font-size:20px">⭐</div>
                        <div>
                            <div class="fc-title">Most Popular Roadmap</div>
                            <div class="fc-sub">TOEIC 700+ · 1.2K learners</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- ===== HOW IT WORKS ===== -->
        <section class="how-section" id="how">
            <div class="container">
                <div class="section-head">
                    <div class="section-tag">How It Works</div>
                    <h2>As Easy As 1 – 2 – 3</h2>
                    <p>No teachers, no payments. Just knowledge and community.</p>
                </div>

                <div class="how-grid">
                    <div class="how-card" id="how-1">
                        <div class="how-num">01</div>
                        <div class="how-icon">✍️</div>
                        <h3>Create Your Roadmap</h3>
                        <p>Turn your English learning experience into a clear roadmap — step by step, week by week.</p>
                    </div>

                    <div class="how-arrow">→</div>

                    <div class="how-card" id="how-2">
                        <div class="how-num">02</div>
                        <div class="how-icon">📤</div>
                        <h3>Share with the Community</h3>
                        <p>Publish your roadmap so thousands of learners can follow it. Become a mentor for the community.</p>
                    </div>

                    <div class="how-arrow">→</div>

                    <div class="how-card" id="how-3">
                        <div class="how-num">03</div>
                        <div class="how-icon">🚀</div>
                        <h3>Learn Together</h3>
                        <p>Join other people’s roadmaps, study step by step, discuss, and improve together.</p>
                    </div>
                </div>
            </div>
        </section>

        <!-- ===== EXPLORE ===== -->
        <section class="explore-section" id="explore">
            <div class="container">
                <div class="section-head">
                    <div class="section-tag">Explore</div>
                    <h2>Popular Community Roadmaps</h2>
                    <p>Created by people who truly succeeded — not advertisements</p>
                </div>

                <div class="filter-bar">
                    <button class="filter-btn active" data-filter="all">All</button>
                    <button class="filter-btn" data-filter="ielts">IELTS</button>
                    <button class="filter-btn" data-filter="toeic">TOEIC</button>
                    <button class="filter-btn" data-filter="giao-tiep">Communication</button>
                    <button class="filter-btn" data-filter="co-ban">Beginner</button>
                </div>

                <div class="roadmap-cards" id="roadmap-cards">

                    <div class="rcard" data-cat="ielts">
                        <div class="rcard-top" style="background:linear-gradient(135deg,#7c3aed,#a855f7)">
                            <div class="rcard-cat">IELTS</div>
                            <div class="rcard-title">From 0 → IELTS 7.0 in 8 Months</div>
                            <div class="rcard-steps">30 learning steps</div>
                        </div>

                        <div class="rcard-body">
                            <div class="rcard-author">
                                <div class="rcard-avatar" style="background:#7c3aed">T</div>
                                <span>Trong Duc · <em>Real IELTS 7.5</em></span>
                            </div>

                            <div class="rcard-meta">
                                <span>👥 2,341 learners</span>
                                <span>⭐ 4.9</span>
                                <span>🕐 8 months</span>
                            </div>

                            <button class="rcard-btn" id="rc-btn-1">Join Now →</button>
                        </div>
                    </div>

                    <!-- Continue translating remaining cards similarly -->

                </div>

                <div style="text-align:center;margin-top:40px">
                    <a href="#" class="btn-outline" id="btn-all-paths">View All Roadmaps →</a>
                </div>
            </div>
        </section>

        <!-- ===== CTA ===== -->
        <section class="cta-section" id="cta">
            <div class="container">
                <div class="cta-box">
                    <div class="cta-bg"></div>

                    <h2>Ready to Start?</h2>

                    <p>
                        Join the largest English learning community in Vietnam — completely free.
                    </p>

                    <div class="cta-btns">
                        <a href="#" class="btn-cta-primary" id="cta-create">
                            ✍️ Create Your First Roadmap
                        </a>

                        <a href="#explore" class="btn-cta-ghost" id="cta-browse">
                            Browse Existing Roadmaps
                        </a>
                    </div>

                    <div class="cta-note">
                        No credit card required · No ads · Free forever
                    </div>
                </div>
            </div>
        </section>

        <!-- ===== FOOTER ===== -->
        <footer class="footer">
            <div class="container">
                <div class="footer-grid">

                    <div class="footer-brand">
                        <a href="${pageContext.request.contextPath}/"
                           class="logo"
                           style="margin-bottom:12px;display:inline-flex">

                            <div class="logo-mark">P</div>
                            <span style="color:#fff">Path<strong>Share</strong></span>
                        </a>

                        <p>
                            A community-driven English learning platform —
                            where everyone can create and share free learning roadmaps.
                        </p>

                        <div class="footer-social">
                            <a href="#" id="fs-fb">FB</a>
                            <a href="#" id="fs-yt">YT</a>
                            <a href="#" id="fs-tt">TT</a>
                            <a href="#" id="fs-dc">DC</a>
                        </div>
                    </div>

                    <div class="footer-col">
                        <h4>Platform</h4>
                        <a href="#">Explore Roadmaps</a>
                        <a href="#">Create Roadmap</a>
                        <a href="#">Leaderboard</a>
                        <a href="#">Community</a>
                    </div>

                    <div class="footer-col">
                        <h4>Support</h4>
                        <a href="#">User Guide</a>
                        <a href="#">FAQ</a>
                        <a href="#">Contact</a>
                        <a href="#">Report a Bug</a>
                    </div>

                    <div class="footer-col">
                        <h4>Legal</h4>
                        <a href="#">Terms of Service</a>
                        <a href="#">Privacy Policy</a>
                        <a href="#">Cookies</a>
                    </div>
                </div>

                <div class="footer-bottom">
                    <span>© 2025 PathShare · Learn Together, Stay Free Forever 💜</span>
                </div>
            </div>
        </footer>

        <script src="assets/js/script.js"></script>

    </body>
</html>