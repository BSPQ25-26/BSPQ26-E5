import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import sushiImage from "../assets/images/delicioso-rollo-sushi-california-aguacate-anguila-pepino_1125744-1587.jpg";

function Home() {
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const [loginType, setLoginType] = useState("customer");
    
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        
        let endpoint = "";
        let payload = {};

        if (loginType === "customer") {
            endpoint = "http://localhost:8080/sessions/users";
            payload = { email: identifier, password: password };
        } else if (loginType === "rider") {
            endpoint = "http://localhost:8080/sessions/riders";
            payload = { dni: identifier, password: password };
        } else if (loginType === "restaurant") {
            endpoint = "http://localhost:8080/sessions/restaurants";
            payload = { email: identifier, password: password };
        } 

        try {
            const response = await fetch(endpoint, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload)
            });

            if (response.status === 501 || response.status === 403) {
                alert("Backend endpoint is NOT IMPLEMENTED yet. Bypassing for UI testing.");
                localStorage.setItem("token", "dummy-dev-token");
                
                if (loginType === "customer") {
                    navigate("/customer-marketplace");
                } else if (loginType === "rider") {
                    navigate("/rider-dashboard");
                } else {
                    alert("Restaurant dashboard coming soon!");
                }
                return;
            }

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem("token", data.token);
                
                if (loginType === "customer") navigate("/customer-marketplace");
                else if (loginType === "rider") navigate("/rider-dashboard");
                else alert("Restaurant dashboard coming soon!");
            } else {
                alert("Login failed. Please check your credentials.");
            }
        } catch (error) {
            alert("Could not connect to the server. Is Spring Boot running?");
        }
    };

    const handleTabSwitch = (type) => {
        setLoginType(type);
        setIdentifier("");
        setPassword("");
    };

    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <h1 className="brand-title">JustOrder</h1>
                    </div>

                    <div className="home-header-right">
                        <nav className="home-nav-links" aria-label="Main navigation" style={{ position: 'relative' }}>
                            <Link to="/register-restaurant" className="nav-link">
                                Register your restaurant
                            </Link>
                            
                            <button 
                                className="nav-link nav-link-button" 
                                type="button" 
                                onClick={() => setIsLoginOpen(!isLoginOpen)}
                                style={{ cursor: "pointer" }}
                            >
                                Log in
                            </button>

                            <Link to="/register-customer" className="nav-link signup-button">
                                Sign up
                            </Link>
                            <Link to="/restaurants/1/menu-editor" className="nav-link menu-editor-button">
                                Edit menu
                            </Link>

                            {isLoginOpen && (
                                <div style={{
                                    position: 'absolute',
                                    top: '40px',
                                    right: '100px',
                                    width: '300px',
                                    backgroundColor: '#ffffff',
                                    boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
                                    borderRadius: '12px',
                                    padding: '20px',
                                    zIndex: 1000,
                                    border: '1px solid #eaeaea'
                                }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '2px solid #f0f0f0', marginBottom: '15px' }}>
                                        <button 
                                            onClick={() => handleTabSwitch('customer')}
                                            style={{ flex: 1, padding: '10px 0', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 'bold', color: loginType === 'customer' ? '#00cc66' : '#999', borderBottom: loginType === 'customer' ? '2px solid #00cc66' : 'none', marginBottom: '-2px' }}
                                        >Customer</button>
                                        <button 
                                            onClick={() => handleTabSwitch('rider')}
                                            style={{ flex: 1, padding: '10px 0', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 'bold', color: loginType === 'rider' ? '#00cc66' : '#999', borderBottom: loginType === 'rider' ? '2px solid #00cc66' : 'none', marginBottom: '-2px' }}
                                        >Rider</button>
                                        <button 
                                            onClick={() => handleTabSwitch('restaurant')}
                                            style={{ flex: 1, padding: '10px 0', border: 'none', background: 'none', cursor: 'pointer', fontWeight: 'bold', color: loginType === 'restaurant' ? '#00cc66' : '#999', borderBottom: loginType === 'restaurant' ? '2px solid #00cc66' : 'none', marginBottom: '-2px' }}
                                        >Restaurant</button>
                                    </div>

                                    <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                                        <input 
                                            type={loginType === 'rider' ? "text" : "email"} 
                                            placeholder={loginType === 'rider' ? "Enter your DNI" : "Enter your Email"}
                                            value={identifier}
                                            onChange={(e) => setIdentifier(e.target.value)}
                                            required
                                            style={{ padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }}
                                        />
                                        <input 
                                            type="password" 
                                            placeholder="Password" 
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                            style={{ padding: '10px', borderRadius: '6px', border: '1px solid #ccc' }}
                                        />
                                        <button 
                                            type="submit"
                                            style={{ padding: '12px', backgroundColor: '#00cc66', color: 'white', border: 'none', borderRadius: '6px', fontWeight: 'bold', cursor: 'pointer', marginTop: '5px' }}
                                        >
                                            Log In
                                        </button>
                                    </form>
                                </div>
                            )}

                        </nav>
                    </div>
                </header>

                <section className="hero-panel">
                    <div className="hero-image-wrap">
                        <img src={sushiImage} alt="Sushi roll on a plate" className="hero-image" />
                    </div>

                    <div className="hero-copy">
                        <h2>
                            DELIVERING HAPPINESS
                            <span>TO YOUR</span>
                            <strong>DOOR</strong>
                        </h2>
                    </div>
                </section>

                <section className="featured-section" aria-labelledby="top-restaurants-heading">
                    <div className="featured-content" />
                </section>
            </section>
        </main>
    );
}

export default Home;