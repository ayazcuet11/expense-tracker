## ADDED Requirements

### Requirement: Profile avatar opens a right-side drawer
The profile avatar in the top-right of the Home header SHALL be tappable. Tapping it SHALL open
a profile panel that slides in from the right edge of the screen as a sidebar drawer with a scrim
over the rest of the app. Tapping the scrim (or a back gesture) SHALL close the drawer.

#### Scenario: Avatar opens the drawer
- **WHEN** the user taps the avatar circle in the Home header
- **THEN** a panel animates in from the right edge over a dimmed scrim

#### Scenario: Scrim tap closes the drawer
- **WHEN** the drawer is open and the user taps the scrim outside the panel
- **THEN** the panel animates back out to the right and the scrim disappears

### Requirement: Drawer shows the user name and theme options
The drawer SHALL display the user's display name and a "Color theme" section containing at least
two themes — Light and Dark — each rendered as a small preview card. The currently active theme's
card SHALL show a checkmark; inactive cards SHALL NOT.

#### Scenario: Drawer contents
- **WHEN** the drawer is open
- **THEN** it shows the user's name and a "Color theme" section with Light and Dark preview cards

#### Scenario: Active theme is marked
- **WHEN** the active theme is Light
- **THEN** the Light preview card shows a checkmark and the Dark card does not

### Requirement: Selecting a theme restyles the entire app
Tapping a theme preview card SHALL make that theme active immediately. The active theme SHALL
determine the colors of every screen (Home, Activity, Stats, Insights, Loans, Add flows), the
bottom bar, bottom sheets, and dialogs — no surface may remain styled by the inactive theme.

#### Scenario: Switching to Dark restyles everything
- **WHEN** the user taps the Dark preview card
- **THEN** the drawer, current screen, and — on navigation — all other screens, sheets, and dialogs render with the dark palette, and the Dark card now shows the checkmark

#### Scenario: Status bar follows the theme
- **WHEN** the Dark theme is active
- **THEN** the system status bar uses the dark background with light icons (and the inverse for Light)

### Requirement: Theme choice persists across restarts
The selected theme SHALL be persisted on device and re-applied when the app is next launched.

#### Scenario: Dark survives a restart
- **WHEN** the user selects Dark, kills the app, and relaunches it
- **THEN** the app starts with the dark palette and the drawer shows Dark as active
