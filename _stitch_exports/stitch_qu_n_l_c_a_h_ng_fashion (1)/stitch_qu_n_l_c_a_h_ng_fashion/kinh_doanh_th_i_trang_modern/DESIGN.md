---
name: Luxe Retailer
colors:
  surface: '#f9f9f9'
  surface-dim: '#dadada'
  surface-bright: '#f9f9f9'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f3f3'
  surface-container: '#eeeeee'
  surface-container-high: '#e8e8e8'
  surface-container-highest: '#e2e2e2'
  on-surface: '#1a1c1c'
  on-surface-variant: '#4a4451'
  inverse-surface: '#2f3131'
  inverse-on-surface: '#f1f1f1'
  outline: '#7c7482'
  outline-variant: '#ccc3d2'
  surface-tint: '#704ba6'
  primary: '#14002f'
  on-primary: '#ffffff'
  primary-container: '#310065'
  on-primary-container: '#9d77d5'
  inverse-primary: '#d7baff'
  secondary: '#4c56af'
  on-secondary: '#ffffff'
  secondary-container: '#97a1ff'
  on-secondary-container: '#29338c'
  tertiary: '#1b0600'
  on-tertiary: '#ffffff'
  tertiary-container: '#3f1500'
  on-tertiary-container: '#ff7a2d'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#eddcff'
  primary-fixed-dim: '#d7baff'
  on-primary-fixed: '#290056'
  on-primary-fixed-variant: '#58328c'
  secondary-fixed: '#dfe0ff'
  secondary-fixed-dim: '#bdc2ff'
  on-secondary-fixed: '#000865'
  on-secondary-fixed-variant: '#333d95'
  tertiary-fixed: '#ffdbcb'
  tertiary-fixed-dim: '#ffb693'
  on-tertiary-fixed: '#341000'
  on-tertiary-fixed-variant: '#7a3000'
  background: '#f9f9f9'
  on-background: '#1a1c1c'
  surface-variant: '#e2e2e2'
  surface-white: '#ffffff'
  accent-amber: '#ffd600'
typography:
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '700'
    lineHeight: 36px
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '700'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  margin-screen: 1.5rem
  spacing-between-elements: 0.5rem
  padding-card: 1rem
  gutter-list: 0.75rem
  unit: 0.5rem
---

## Brand & Style

The brand identity for **Luxe Retailer** is sophisticated, premium, and airy. It targets fashion-forward individuals and boutique owners who value organization and modern aesthetics. 

The design style is a blend of **Modern Minimalism** and **Glassmorphism**. It utilizes heavy whitespace, a refined color palette, and soft ambient glows to create a sense of depth and luxury. The emotional response should be one of "effortless chic"—the interface feels high-end yet approachable, mirroring the experience of walking into a minimalist, sun-drenched boutique.

## Colors

The color strategy relies on a sophisticated "deep plum" primary and a "burnt orange" tertiary accent used for high-impact actions.

- **Primary & Secondary:** Deep purples and muted indigos provide a professional, trustworthy foundation.
- **Accent:** The tertiary color (#ff7a2d) is used specifically for the main "Call to Action" to create a warm, energetic focal point.
- **Atmospheric Tones:** We use "Fixed" variants (soft pastels) like `primary-fixed` and `tertiary-fixed` as large, blurred background glows to soften the white canvas.
- **Neutrals:** The background is a crisp `surface-white`, while `on-surface-variant` (a soft charcoal) is used for secondary body text to maintain high legibility without the harshness of pure black.

## Typography

The system uses **Inter** exclusively to maintain a clean, utilitarian, and modern feel that doesn't distract from the fashion imagery.

- **Headlines:** Use Bold (700) or Semi-Bold (600) weights with tight tracking (-2%) to feel modern and "editorial."
- **Body:** Standard weights (400) ensure high readability in product descriptions.
- **Labels:** Use Semi-Bold (600) for buttons and navigation items to provide a clear interactive hierarchy. 
- **Hierarchy:** We prioritize a clear vertical rhythm by using larger line-heights for body text to maintain the "airy" feel of the brand.

## Layout & Spacing

The system follows a **Fluid Grid** approach for mobile, with fixed safe margins on the sides.

- **Margins:** A standard 24px (1.5rem) screen margin is used to keep content away from edges.
- **Vertical Rhythm:** Elements within a group are spaced by 8px (0.5rem) increments. Large sections or major UI blocks are separated by 32px or more to emphasize the minimalist aesthetic.
- **Responsive Behavior:** On mobile, the layout is a single column. On larger screens, the central container maintains a maximum width of 428px (mobile-first preview) or expands into a multi-column grid for tablet/desktop gallery views.

## Elevation & Depth

Depth is achieved through **Ambient Shadows** and **Tonal Layering** rather than traditional heavy dropshadows.

- **Shadows:** Use extremely diffused, low-opacity shadows (e.g., `rgba(0,0,0,0.08)`) with a large blur radius (30px+) for cards and images.
- **Interactive Depth:** Buttons use tinted shadows that match the button's hue (e.g., a soft orange shadow for the orange CTA) to create a glowing effect.
- **Atmosphere:** Use large, blurred background circles (blur-80px to 60px) in secondary and tertiary colors to create a sense of three-dimensional space behind the main content canvas.

## Shapes

The shape language is **Ultra-Rounded (Pill-shaped)**, conveying a soft, friendly, and modern tone.

- **Primary Buttons:** Always use full rounding (`rounded-full`) to appear inviting and tactile.
- **Hero Containers:** Use a large radius (32px or `rounded-[32px]`) to frame imagery elegantly.
- **Cards & Inputs:** Standard elements should use `rounded-xl` (12px-16px) to maintain consistency with the rounded theme.

## Components

- **Primary Button:** High-contrast background (`on-tertiary-container`), `label-lg` typography, `rounded-full`, and a tinted shadow that matches the background color. 56px height.
- **Secondary/Text Button:** Transparent background, `on-surface-variant` text, `rounded-full`. 48px height. Subtle background hover state (e.g., `surface-container-low`).
- **Hero Image:** Rounded corners (32px), subtle inner gradient overlay at the bottom to ensure depth, and a slight scale-up transform (1.02x) on hover.
- **Interactive States:** Use smooth transitions (200ms) for all scale and color changes to maintain a premium, fluid feel.
- **Status Bars/Indicators:** Use the `accent-amber` or `primary` colors for non-intrusive status indicators or notification dots.