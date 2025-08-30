-- Migration V4: Create Shops and Menu Items Tables for Shop Onboarding Functionality
-- This migration adds comprehensive shop management and menu system capabilities

-- Create shops table
CREATE TABLE shops (
    id BIGSERIAL PRIMARY KEY,
    shop_name VARCHAR(100) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(20),
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    description TEXT,
    shop_type VARCHAR(50) NOT NULL CHECK (shop_type IN (
        'RESTAURANT', 'CAFE', 'BAKERY', 'FAST_FOOD', 'GROCERY', 
        'ELECTRONICS', 'CLOTHING', 'PHARMACY', 'BOOKSTORE', 'OTHER'
    )),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN (
        'PENDING', 'ACTIVE', 'SUSPENDED', 'INACTIVE'
    )),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create menu_items table
CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    item_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    category VARCHAR(50) NOT NULL CHECK (category IN (
        'APPETIZER', 'MAIN_COURSE', 'DESSERT', 'BEVERAGE', 'SNACK',
        'BREAKFAST', 'LUNCH', 'DINNER', 'SALAD', 'SOUP', 'PIZZA',
        'BURGER', 'SANDWICH', 'PASTA', 'SEAFOOD', 'VEGETARIAN',
        'VEGAN', 'BAKERY', 'ICE_CREAM', 'COFFEE', 'TEA', 'JUICE', 'OTHER'
    )),
    is_available BOOLEAN DEFAULT TRUE,
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    preparation_time_minutes INTEGER,
    image_url VARCHAR(200),
    shop_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shops(id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_shops_email ON shops(email);
CREATE INDEX idx_shops_status ON shops(status);
CREATE INDEX idx_shops_shop_type ON shops(shop_type);
CREATE INDEX idx_shops_city ON shops(city);
CREATE INDEX idx_shops_state ON shops(state);
CREATE INDEX idx_shops_country ON shops(country);
CREATE INDEX idx_shops_registration_date ON shops(registration_date);
CREATE INDEX idx_shops_shop_name ON shops(shop_name);
CREATE INDEX idx_shops_owner_name ON shops(owner_name);

CREATE INDEX idx_menu_items_shop_id ON menu_items(shop_id);
CREATE INDEX idx_menu_items_category ON menu_items(category);
CREATE INDEX idx_menu_items_is_available ON menu_items(is_available);
CREATE INDEX idx_menu_items_is_vegetarian ON menu_items(is_vegetarian);
CREATE INDEX idx_menu_items_is_vegan ON menu_items(is_vegan);
CREATE INDEX idx_menu_items_price ON menu_items(price);
CREATE INDEX idx_menu_items_item_name ON menu_items(item_name);
CREATE INDEX idx_menu_items_created_at ON menu_items(created_at);

-- Create composite indexes for common queries
CREATE INDEX idx_shops_city_state ON shops(city, state);
CREATE INDEX idx_shops_status_type ON shops(status, shop_type);
CREATE INDEX idx_menu_items_shop_category ON menu_items(shop_id, category);
CREATE INDEX idx_menu_items_shop_available ON menu_items(shop_id, is_available);
CREATE INDEX idx_menu_items_shop_vegetarian ON menu_items(shop_id, is_vegetarian);
CREATE INDEX idx_menu_items_shop_vegan ON menu_items(shop_id, is_vegan);

-- Add unique constraint to prevent duplicate shop names in the same city
CREATE UNIQUE INDEX idx_shops_unique_name_city ON shops(shop_name, city);

-- Add unique constraint to prevent duplicate menu item names within the same shop
CREATE UNIQUE INDEX idx_menu_items_unique_name_shop ON menu_items(shop_id, item_name);

-- Create trigger to automatically update updated_at timestamp for shops
CREATE OR REPLACE FUNCTION update_shops_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_shops_updated_at
    BEFORE UPDATE ON shops
    FOR EACH ROW
    EXECUTE FUNCTION update_shops_updated_at();

-- Create trigger to automatically update updated_at timestamp for menu_items
CREATE OR REPLACE FUNCTION update_menu_items_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_menu_items_updated_at
    BEFORE UPDATE ON menu_items
    FOR EACH ROW
    EXECUTE FUNCTION update_menu_items_updated_at();

-- Insert sample shop data for testing
INSERT INTO shops (shop_name, owner_name, email, phone_number, city, state, country, pincode, description, shop_type, status) VALUES
('Pizza Palace', 'Mario Rossi', 'mario@pizzapalace.com', '+1234567890', 'New York', 'NY', 'United States', '10001', 'Authentic Italian pizzas and pasta', 'RESTAURANT', 'ACTIVE'),
('Coffee Corner', 'Sarah Johnson', 'sarah@coffeecorner.com', '+1234567891', 'Toronto', 'ON', 'Canada', 'M5H2N2', 'Premium coffee and light snacks', 'CAFE', 'ACTIVE'),
('Fresh Bakery', 'James Smith', 'james@freshbakery.com', '+1234567892', 'London', 'England', 'United Kingdom', 'SW1A1AA', 'Fresh baked goods daily', 'BAKERY', 'PENDING'),
('Tech Store', 'David Wilson', 'david@techstore.com', '+1234567893', 'Sydney', 'NSW', 'Australia', '2000', 'Latest electronics and gadgets', 'ELECTRONICS', 'ACTIVE'),
('Spice Garden', 'Priya Sharma', 'priya@spicegarden.com', '+1234567894', 'Mumbai', 'Maharashtra', 'India', '400001', 'Authentic Indian cuisine', 'RESTAURANT', 'ACTIVE');

-- Insert sample menu items for the shops
INSERT INTO menu_items (item_name, description, price, category, is_available, is_vegetarian, is_vegan, preparation_time_minutes, shop_id) VALUES
-- Pizza Palace menu items
('Margherita Pizza', 'Classic tomato, mozzarella, and basil pizza', 12.99, 'PIZZA', TRUE, TRUE, FALSE, 15, 1),
('Pepperoni Pizza', 'Pepperoni with mozzarella cheese', 14.99, 'PIZZA', TRUE, FALSE, FALSE, 15, 1),
('Spaghetti Carbonara', 'Creamy pasta with bacon and parmesan', 13.99, 'PASTA', TRUE, FALSE, FALSE, 20, 1),
('Caesar Salad', 'Fresh romaine lettuce with caesar dressing', 8.99, 'SALAD', TRUE, TRUE, FALSE, 10, 1),

-- Coffee Corner menu items
('Espresso', 'Strong Italian coffee', 2.99, 'COFFEE', TRUE, TRUE, TRUE, 3, 2),
('Cappuccino', 'Espresso with steamed milk foam', 4.99, 'COFFEE', TRUE, TRUE, FALSE, 5, 2),
('Blueberry Muffin', 'Fresh baked blueberry muffin', 3.99, 'BAKERY', TRUE, TRUE, FALSE, 2, 2),
('Avocado Toast', 'Whole grain bread with fresh avocado', 7.99, 'BREAKFAST', TRUE, TRUE, TRUE, 8, 2),

-- Fresh Bakery menu items
('Croissant', 'Buttery French pastry', 2.99, 'BAKERY', TRUE, TRUE, FALSE, 1, 3),
('Chocolate Cake', 'Rich chocolate layer cake', 4.99, 'DESSERT', TRUE, TRUE, FALSE, 1, 3),
('Sourdough Bread', 'Artisan sourdough loaf', 5.99, 'BAKERY', TRUE, TRUE, TRUE, 1, 3),

-- Tech Store menu items (snacks/beverages available in store)
('Energy Drink', 'Boost your energy while shopping', 2.99, 'BEVERAGE', TRUE, TRUE, TRUE, 1, 4),
('Protein Bar', 'Healthy snack option', 3.99, 'SNACK', TRUE, TRUE, TRUE, 1, 4),

-- Spice Garden menu items
('Butter Chicken', 'Creamy tomato-based chicken curry', 15.99, 'MAIN_COURSE', TRUE, FALSE, FALSE, 25, 5),
('Palak Paneer', 'Spinach curry with cottage cheese', 12.99, 'MAIN_COURSE', TRUE, TRUE, FALSE, 20, 5),
('Vegan Dal', 'Lentil curry with spices', 10.99, 'MAIN_COURSE', TRUE, TRUE, TRUE, 18, 5),
('Mango Lassi', 'Sweet yogurt drink with mango', 4.99, 'BEVERAGE', TRUE, TRUE, FALSE, 5, 5),
('Masala Chai', 'Spiced Indian tea', 2.99, 'TEA', TRUE, TRUE, TRUE, 7, 5);

-- Add comments for documentation
COMMENT ON TABLE shops IS 'Stores information about registered shops in the order management system';
COMMENT ON TABLE menu_items IS 'Stores menu items for each shop with pricing and dietary information';
COMMENT ON COLUMN shops.status IS 'Shop approval status: PENDING (awaiting approval), ACTIVE (approved and operational), SUSPENDED (temporarily disabled), INACTIVE (permanently disabled)';
COMMENT ON COLUMN shops.shop_type IS 'Category of the shop business type';
COMMENT ON COLUMN menu_items.is_available IS 'Whether the menu item is currently available for ordering';
COMMENT ON COLUMN menu_items.is_vegetarian IS 'Whether the item contains no meat (but may contain dairy/eggs)';
COMMENT ON COLUMN menu_items.is_vegan IS 'Whether the item contains no animal products whatsoever';
