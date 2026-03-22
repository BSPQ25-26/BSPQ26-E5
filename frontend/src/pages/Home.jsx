import { Link } from "react-router-dom";
import sushiImage from "../assets/images/delicioso-rollo-sushi-california-aguacate-anguila-pepino_1125744-1587.jpg";
import cartImage from "../assets/images/Shopping cart.png";

function Home() {
    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <button className="menu-button" type="button" aria-label="Open navigation menu">
                            <span />
                            <span />
                            <span />
                        </button>
                        <h1 className="brand-title">JustOrder</h1>
                    </div>

                    <div className="home-header-right">
                        <nav className="home-nav-links" aria-label="Main navigation">
                            <Link to="/register-restaurant" className="nav-link">
                                Register your restaurant
                            </Link>
                            <button className="nav-link nav-link-button" type="button" disabled aria-disabled="true">
                                Log in
                            </button>
                            <Link to="/register-customer" className="nav-link signup-button">
                                Sign up
                            </Link>
                            <Link to="/restaurants/1/menu-editor" className="nav-link menu-editor-button">
                                Edit menu
                            </Link>
                        </nav>

                        <Link to="/checkout" className="cart-link" aria-label="Go to cart">
                            <img src={cartImage} alt="Shopping cart" className="cart-icon" />
                        </Link>
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