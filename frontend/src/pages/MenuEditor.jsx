import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { 
  getAllergens, 
  getMenuByRestaurantId, 
  createDish, 
  updateDish, 
  deleteDish 
} from "../api/authApi";
import "../assets/css/MenuEditor.css";

const RESTAURANT_ID = 1; //WE WILL CHANGE IT WHEN WE IMPLEMENT AUTH

const EMPTY_FORM = {
  name: "",
  description: "",
  price: "",
  allergenNames: [],
};

function MenuEditor() {
  const [allergens, setAllergens] = useState([]);
  const [allergensLoading, setAllergensLoading] = useState(true);
  const [allergensError, setAllergensError] = useState("");
  const [dishes, setDishes] = useState([]);
  const [dishesLoading, setDishesLoading] = useState(true);
  const [dishesError, setDishesError] = useState("");
  const [form, setForm] = useState(EMPTY_FORM);
  const [editingDishId, setEditingDishId] = useState(null);
  const [message, setMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Load allergens and dishes on mount
  useEffect(() => {
    const load = async () => {
      try {
        setAllergensLoading(true);
        const allergenNames = await getAllergens();
        setAllergens(allergenNames);
        setAllergensError("");
      } catch (error) {
        setAllergensError("Error loading allergens: " + error.message);
        setAllergens([]);
      } finally {
        setAllergensLoading(false);
      }
    };

    const loadDishes = async () => {
      try {
        setDishesLoading(true);
        const menu = await getMenuByRestaurantId(RESTAURANT_ID);
        setDishes(Array.isArray(menu) ? menu : []);
        setDishesError("");
      } catch (error) {
        setDishesError("Error loading dishes: " + error.message);
        setDishes([]);
      } finally {
        setDishesLoading(false);
      }
    };

    load();
    loadDishes();
  }, []);

  const isEditing = editingDishId !== null;

  const onFieldChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const onAllergenChange = (event) => {
    const { value, checked } = event.target;

    // Keep allergen selection in sync with checkbox state.
    setForm((prev) => ({
      ...prev,
      allergenNames: checked
        ? [...prev.allergenNames, value]
        : prev.allergenNames.filter((allergen) => allergen !== value),
    }));
  };

  const resetForm = () => {
    setForm(EMPTY_FORM);
    setEditingDishId(null);
  };

  const onSubmit = async (event) => {
    event.preventDefault();

    const priceNumber = Number(form.price);

    if (!form.name.trim()) {
      setMessage("Dish name is required");
      return;
    }

    if (!form.description.trim()) {
      setMessage("Description is required");
      return;
    }

    if (Number.isNaN(priceNumber) || priceNumber <= 0) {
      setMessage("Price must be greater than 0");
      return;
    }

    setIsSubmitting(true);

    try {
      // Normalize values before sending to the API.
      const dishPayload = {
        name: form.name.trim(),
        description: form.description.trim(),
        price: Number(priceNumber.toFixed(2)),
        allergenNames: form.allergenNames,
      };

      if (isEditing) {
        await updateDish(editingDishId, dishPayload);
        setMessage("Dish updated successfully");
      } else {
        await createDish(RESTAURANT_ID, dishPayload);
        setMessage("Dish created successfully");
      }

      // Reload dishes from backend
      const menu = await getMenuByRestaurantId(RESTAURANT_ID);
      setDishes(Array.isArray(menu) ? menu : []);
      resetForm();
    } catch (error) {
      setMessage("Error: " + error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const onEditDish = (dish) => {
    // Load selected dish into the form to edit it in place.
    setForm({
      name: dish.name,
      description: dish.description,
      price: String(dish.price),
      allergenNames: dish.allergenNames || [],
    });
    setEditingDishId(dish.id);
    setMessage("");
  };

  const onDeleteDish = async (dishId) => {
    setIsSubmitting(true);

    try {
      await deleteDish(dishId);
      // Update local list immediately after a successful deletion.
      setDishes((prev) => prev.filter((dish) => dish.id !== dishId));

      if (editingDishId === dishId) {
        resetForm();
      }

      setMessage("Dish deleted successfully");
    } catch (error) {
      setMessage("Error deleting dish: " + error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main className="menu-editor-page">
      <section className="menu-editor-panel">
        <div className="menu-editor-header">
          <p className="menu-editor-kicker">Restaurant 1</p>
          <h1>Menu Editor</h1>
          <p>Manage restaurant dishes.</p>
        </div>

        <div className="menu-editor-grid">
          <form className="menu-editor-form" onSubmit={onSubmit}>
            <h2>{isEditing ? "Edit dish" : "New dish"}</h2>

            <label htmlFor="dish-name">Name</label>
            <input
              id="dish-name"
              name="name"
              type="text"
              placeholder="E.g. Margherita pizza"
              value={form.name}
              onChange={onFieldChange}
              disabled={isSubmitting}
              required
            />

            <label htmlFor="dish-description">Description</label>
            <textarea
              id="dish-description"
              name="description"
              rows="3"
              placeholder="Describe ingredients and preparation"
              value={form.description}
              onChange={onFieldChange}
              disabled={isSubmitting}
              required
            />

            <label htmlFor="dish-price">Price (EUR)</label>
            <input
              id="dish-price"
              name="price"
              type="number"
              min="0.01"
              step="0.01"
              placeholder="0.00"
              value={form.price}
              onChange={onFieldChange}
              disabled={isSubmitting}
              required
            />

            <fieldset>
              <legend>Allergens</legend>
              {allergensLoading && <p className="allergen-loading">Loading allergens...</p>}
              {allergensError && <p className="allergen-error">{allergensError}</p>}
              {!allergensLoading && allergens.length > 0 && (
                <div className="allergen-list">
                  {allergens.map((allergen) => (
                    <label key={allergen} className="allergen-chip">
                      <input
                        type="checkbox"
                        value={allergen}
                        checked={form.allergenNames.includes(allergen)}
                        onChange={onAllergenChange}
                        disabled={isSubmitting}
                      />
                      <span>{allergen}</span>
                    </label>
                  ))}
                </div>
              )}
              {!allergensLoading && allergens.length === 0 && !allergensError && (
                <p className="allergen-empty">No allergens available.</p>
              )}
            </fieldset>

            <div className="menu-editor-actions">
              <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                {isSubmitting ? "Saving..." : (isEditing ? "Save changes" : "Create dish")}
              </button>
              {isEditing && (
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={resetForm}
                  disabled={isSubmitting}
                >
                  Cancel edit
                </button>
              )}
            </div>

            {message && <p className="menu-editor-message">{message}</p>}
          </form>

          <section className="menu-editor-list">
            <h2>Current dishes</h2>
            {dishesLoading && <p className="loading-message">Loading dishes...</p>}
            {dishesError && <p className="error-message">{dishesError}</p>}
            {!dishesLoading && dishes.length === 0 && !dishesError && (
              <p className="empty-message">No dishes yet.</p>
            )}
            {!dishesLoading && dishes.length > 0 && (
              <ul>
                {dishes.map((dish) => (
                  <li key={dish.id} className="dish-card">
                    <div>
                      <h3>{dish.name}</h3>
                      <p className="dish-description">{dish.description || "No description"}</p>
                      <p className="dish-price">{dish.price.toFixed(2)} EUR</p>
                      <p className="dish-allergens">
                        <strong>Allergens:</strong>{" "}
                        {dish.allergenNames && dish.allergenNames.length > 0
                          ? dish.allergenNames.join(", ")
                          : "None"}
                      </p>
                    </div>
                    <div className="dish-actions">
                      <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={() => onEditDish(dish)}
                        disabled={isSubmitting}
                      >
                        Edit
                      </button>
                      <button
                        type="button"
                        className="btn btn-danger"
                        onClick={() => onDeleteDish(dish.id)}
                        disabled={isSubmitting}
                      >
                        Delete
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </section>
        </div>

        <div className="menu-editor-footer-link">
          <Link to="/" className="btn btn-secondary">
            Back to home
          </Link>
        </div>
      </section>
    </main>
  );
}

export default MenuEditor;

